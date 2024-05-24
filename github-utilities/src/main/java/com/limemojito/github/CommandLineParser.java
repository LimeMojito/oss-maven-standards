/*
 * Copyright 2011-2024 Lime Mojito Pty Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.limemojito.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repos;
import com.jcabi.http.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jcabi.http.Request.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Component
@Slf4j
public class CommandLineParser implements CommandLineRunner {

    private final Github gitHub;
    private final String organization;
    private final String repository;
    private final String ownerTeam;
    private final String developTeam;
    private final int ownerTeamId;
    private final boolean isPublic;
    private final String description;
    private final int workflowRepositoryId;

    @SuppressWarnings("ParameterNumber")
    public CommandLineParser(Github gitHub,
                             @Value("${github.org}") String organization,
                             @Value("${github.workflow.repository.id}") int workflowRepositoryId,
                             @Value("${github.owner.team}") String ownerTeam,
                             @Value("${github.owner.team.id}") int ownerTeamId,
                             @Value("${github.develop.team}") String developTeam,
                             @Value("${github.repository}") String repository,
                             @Value("${github.repository.public}") boolean isPublic,
                             @Value("${github.repository.description}") String description) {
        this.gitHub = gitHub;
        this.organization = organization;
        this.repository = repository;
        this.ownerTeam = ownerTeam;
        this.developTeam = developTeam;
        this.ownerTeamId = ownerTeamId;
        this.workflowRepositoryId = workflowRepositoryId;
        this.isPublic = isPublic;
        this.description = description;
    }

    @Override
    public void run(String... args) throws Exception {
        final Coordinates.Simple repo = new Coordinates.Simple(organization, this.repository);
        create(repo);
        updateSettings(repo);
        actionPermissions(repo);
        applyAutomatedSecurityFixes(repo);
        applyTeamAccess(repo);
        applyRules(repo);
        log.info("Updated %b repository %s".formatted(isPublic, repo));
    }

    private void actionPermissions(Coordinates.Simple repo) throws IOException {
        gitHub.entry().method(PUT).uri().path("repos/%s/actions/permissions".formatted(repo)).back()
              .body().set(json(Map.of(
                      "enabled", true,
                      "allowed_actions", "all"
              )))
              .back()
              .fetch();
    }

    private void create(Coordinates.Simple repo) throws IOException {
        if (!gitHub.repos().exists(repo)) {
            log.info("Creating repository {}/{}", organization, repository);
            gitHub.repos()
                  .create(new Repos.RepoCreate(repository, !isPublic).withOrganization(organization)
                                                                     .withDescription(description));
        }
    }

    private void applyRules(Coordinates.Simple coords) throws IOException {
        final String path = "/repos/%s/rulesets".formatted(coords);
        final String body = gitHub.entry()
                                  .method(GET).uri().path(path).back()
                                  .fetch()
                                  .body();
        final int assumeEmptySize = 10;
        if (body.length() < assumeEmptySize) {
            log.info("Creating ruleset");
            final JsonStructure json = ruleSet();
            final int status = gitHub.entry().method(POST).uri().path(path).back()
                                     .body().set(json).back()
                                     .fetch()
                                     .status();
            if (status != CREATED.value()) {
                throw new IOException("Could not generate ruleset %s".formatted(json));
            }
        }
    }

    private JsonStructure ruleSet() {
        final List<Map<String, ?>> rules = new ArrayList<>(List.of(
                Map.of("type", "deletion"),
                Map.of("type", "required_signatures"),
                Map.of("type", "non_fast_forward"),
                Map.of("type", "pull_request",
                       "parameters", Map.of(
                                "required_approving_review_count", 1,
                                "dismiss_stale_reviews_on_push", true,
                                "require_code_owner_review", true,
                                "require_last_push_approval", false,
                                "required_review_thread_resolution", true
                        ))
        ));
        if (isPublic) {
            rules.add(Map.of("type", "workflows",
                             "parameters", Map.of(
                            "workflows", List.of(
                                    Map.of(
                                            "path", ".github/workflows/" + "oss-java-build.yml",
                                            "repository_id", workflowRepositoryId,
                                            "ref", "master"
                                    )
                            ))
            ));
        }
        return json(Map.of(
                "name", "SignAndSeal",
                "target", "branch",
                "enforcement", "active",
                "bypass_actors", List.of(
                        Map.of("actor_id", ownerTeamId,
                               "actor_type", "Team",
                               "bypass_mode", "always")
                ),
                "conditions", Map.of(
                        "ref_name", Map.of(
                                "exclude", List.of(),
                                "include", List.of("~DEFAULT_BRANCH")
                        )
                ),
                "rules", rules
        ));
    }

    private void applyTeamAccess(Coordinates.Simple repo) throws IOException {
        applyPut("/orgs/%s/teams/%s/repos/%s".formatted(organization, ownerTeam, repo),
                 Map.of("permission", "admin"));
        applyPut("/orgs/%s/teams/%s/repos/%s".formatted(organization, developTeam, repo),
                 Map.of("permission", "push"));
    }

    private void applyAutomatedSecurityFixes(Coordinates.Simple coords) throws IOException {
        applyPut("repos/%s/automated-security-fixes".formatted(coords));
    }

    private void applyPut(String uri, Map<String, Object> body) throws IOException {
        Request request = gitHub.entry()
                                .method(PUT)
                                .uri()
                                .path(uri)
                                .back();
        if (body != null) {
            request = request.body()
                             .set(json(body))
                             .back();
        }
        final int status = request
                .fetch()
                .status();
        if (status != NO_CONTENT.value()) {
            throw new IOException("Could not activate %s".formatted(uri));
        }
    }

    private void applyPut(String uri) throws IOException {
        applyPut(uri, null);
    }

    private void updateSettings(Coordinates.Simple coords) throws IOException {
        final JsonObjectBuilder builder = Json.createObjectBuilder()
                                              .add("private", !isPublic)
                                              .add("has_issues", isPublic)
                                              .add("has_projects", false)
                                              .add("has_wiki", false)
                                              .add("allow_auto_merge", !isPublic)
                                              .add("allow_rebase_merge", false)
                                              .add("delete_branch_on_merge", true)
                                              .add("allow_update_branch", true)
                                              .add("squash_merge_commit_title", "COMMIT_OR_PR_TITLE")
                                              .add("squash_merge_commit_message", "COMMIT_MESSAGES")
                                              .add("merge_commit_title", "MERGE_MESSAGE")
                                              .add("merge_commit_message", "PR_TITLE")
                                              .add("web_commit_signoff_required", "true");
        if (isPublic) {
            builder.add("security_and_analysis", securitySetup());
        }
        final JsonObject json = builder.build();
        log.info("Updating {} to {}", coords, json);
        gitHub.repos().get(coords).patch(json);
    }

    private static JsonObject securitySetup() {
        final Map<String, Object> map = Map.of(
                "secret_scanning", Map.of("status", "enabled"),
                "secret_scanning_push_protection", Map.of("status", "enabled")
        );
        return json(map);
    }

    private static JsonObject json(Map<String, Object> map) {
        return Json.createObjectBuilder(map).build();
    }

}
