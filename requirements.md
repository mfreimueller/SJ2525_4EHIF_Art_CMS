# High-Level Requirements

## Implement Service Layer

- Implement all services for all entities
- Implement full CRUD functionality (+ replace) for all non-abstract entities
- For Content, provide Read functionality
- Test all services (make sure to have a high test coverage)

## Implement Presentation Layer

- Write HATEOS-based REST(ful) Controller for each entity
- Provide all CRUD operations via the correct HTTP verbs for all non-abstract entities
- For Content, provide Read functionality
- Endpoints for PointOfInterest, Controller, Visitor and VisitHistory should be pageable and support sortingadd
- Test all REST(ful) Controllers (make sure to have a high test coverage)

## Implement Thymeleaf

- Write Thymeleaf templates for all non-abstract entities supporting CRUD operations
- Write the necessary controllers for those templates as well
- For Content, provide Thymeleaf templates supporting Read operations
- Create the following navigational hierarchy: Exhibition -> {`Point Of Interest` -> {`*Content`},`Collection[<> Exhibition]` -> {`Point Of Interest`}
- Provide a 'Content Pool' and 'Point of Interest Pool' page listing all non-assigned content or POI contents
- In the pools, it should be possible to create new entities of the given type
- Provide a login & registration page
- Nothing should be accessible without proper authentication (exception login & registration)
- Visitor & VisitHistory should not be createable in the web interface, as these entities are created by mobile apps

## Implement Spring Security

- Implement Spring Security to secure any pages that aren't login or registration

## Provide admin Access

- One admin Creators should always exist with (unsafe) credentials: Username=admin, Password=admin
- If admin Creators already exists, don't recreate

## Implement role-based functionality

- Editor Creators (role = Creator.Role.EDITOR) are able to perform all CRUD operations
- Viewer Creators (role = Creator.Role.VIEWER) are able to perform Read operations
- Admin Creators (role = Creator.Role.ADMIN) are able to manage users and perform all CRUD operations

## Implement RestClient for PointOfInterestController

- Add a new endpoint /api/pois/{id}/comments that returns two comments per POI
- Use RestClient to retrieve comments from dummyjson.com
- Use `https://dummyjson.com/comments?limit=2&skip={random_offset}&select=body` to retrieve two random comments, where `random_offset` must be between 0 and 98