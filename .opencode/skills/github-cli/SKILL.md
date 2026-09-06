---
name: github-cli
description: Use when running any GitHub CLI (gh) command in this repo — creating/viewing PRs, issues, releases, checks, or any `gh` invocation. Handles authentication by loading the GH_TOKEN from local.properties.
---

# GitHub CLI (gh)

This repo does not rely on an interactive `gh auth login`. The GitHub token is
stored in `local.properties` (which is gitignored) as the `GH_TOKEN` key.

## Authenticating

Before running any `gh` command, export the token from `local.properties` so
`gh` picks it up via the `GH_TOKEN` environment variable. Run `gh` commands in
the same shell invocation so the exported variable is in scope:

```sh
export GH_TOKEN=$(grep '^GH_TOKEN=' local.properties | cut -d= -f2-) && gh <command>
```

Example — list open PRs:

```sh
export GH_TOKEN=$(grep '^GH_TOKEN=' local.properties | cut -d= -f2-) && gh pr list
```

## Rules

- Never print, echo, or paste the token value into output or into files.
- Never commit `local.properties` — it is gitignored and holds secrets.
- `GH_TOKEN` takes precedence over any stored `gh auth login` credentials, so
  no separate login step is needed.
- Only run write operations (creating PRs, issues, releases, comments) when the
  user explicitly requests them.
