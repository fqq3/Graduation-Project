---
name: community-project-rule
description: Use this for the user's community property management platform. Applies strict rules for frontend migration, backend SQL/API changes, PRD execution, word cloud analysis, health scoring, parking peak analysis, and dashboard features.
---

# Community Project Rule Skill

Use this skill for the community property management platform.

## Project Context

Repository structure:

- `community/`: backend
- `updateUI/`: frontend

The user requires:

- UI unchanged
- Interaction unchanged
- Data fields unchanged unless explicitly required
- Minimal changes only
- No unrelated files
- No unnecessary dependencies
- Field mapping and data source before SQL

## Required Workflow

For every feature or fix:

1. Read relevant files.
2. List affected files.
3. List files that should not be changed.
4. List field mapping and data source if data is involved.
5. Ask the user for missing business rules.
6. Produce a file-level task table.
7. Implement only after the plan is clear.
8. Review diffs for unrelated changes.

## Community Feature Rules

For dashboard/statistics/analysis features, always clarify:

- Community scope
- Time range
- Default time range
- Data source table
- Type/status field
- Whether deleted data should be excluded
- Count/rate formula
- Empty-data behavior
- Sorting rule
- Whether frontend chart is clickable
- Whether list/detail drilldown is required

## Word Cloud Rules

For complaint/suggestion word cloud tasks, clarify:

- Source table
- Text column
- Type field
- Complaint type value
- Suggestion type value
- Community ID filter
- Time range filter
- Stop-word handling
- Minimum word length
- Top N words
- Empty result behavior

## Health Score Rules

For health score tasks, clarify:

- Abnormal device count source
- Pending repair count source
- Complaint/suggestion count source
- Payment rate source
- Warning threshold
- Abnormal threshold
- Card color rule
- Score formula
- Whether thresholds are configurable

## Parking Peak Rules

For parking peak analysis, clarify:

- Source table
- Entry time field
- Exit time field
- Time grouping rule
- Date filter
- Community filter
- Whether overnight parking is counted
- Whether duplicate records are excluded