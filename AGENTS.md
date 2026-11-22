# Community Property Management Project Rules

## Project Structure

This repository contains:

- `community/`: backend project
- `updateUI/`: frontend project

## Hard Constraints

For this project:

1. Do not modify unrelated files.
2. Preserve existing UI.
3. Preserve existing interactions.
4. Preserve existing field names.
5. Preserve existing route order.
6. Preserve existing menu names.
7. Preserve existing API contracts unless explicitly requested.
8. Do not add dependencies unless approved.
9. Do not refactor unrelated code.
10. Make only necessary compatibility or feature changes.

## Frontend Rules

Frontend stack:

- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router 4
- Axios

When modifying frontend:

- Do not change login page unless explicitly requested.
- Do not change visible copy unless explicitly requested.
- Do not change component layout unless explicitly requested.
- Do not change form fields unless explicitly requested.
- Do not change table columns unless explicitly requested.
- Do not change route path/menu name unless explicitly requested.
- Maintain old Vue2/Element UI behavior where migration compatibility is the task.

## Backend Rules

Backend stack:

- Spring Boot
- MyBatis / MyBatis-Plus
- Java
- Mapper XML

When modifying backend:

- Controller, Service, Mapper, XML must stay consistent.
- SQL fields must match DTO/VO/entity fields.
- Do not duplicate Mapper XML statement IDs.
- Do not change global security config unless required.
- Do not change database schema unless approved.
- Do not change BaseEntity inheritance or time field types unless required.

## SQL Output Rule

For all SQL-related tasks, output in this order:

1. Field mapping
2. Data source
3. Join relationship
4. Filter conditions
5. Final SQL
6. Modified files
7. Verification method

## Verification

Before claiming completion, provide:

- Frontend build or run command if frontend changed
- Backend compile or test command if backend changed
- Manual browser/API verification steps
- Known risks