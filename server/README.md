# Mock Employee API

This Spring Boot application mocks employee data to facilitate implementation of required endpoints in API module.

**It should not be modified.**

### Getting Started

Start **Server** Spring Boot application
`./gradlew server:bootRun`

Each invocation of **Server** application triggers a new list of mock employee data. While testing, you'll want to keep
this server running if your test requires consistent data. Additionally, the web server will randomly choose when to rate
limit requests, so keep this mind when designing/implementing the actual Employee API.

_Note_: Console logs each mock employee upon startup.

### Endpoints

    request:
        method: GET
        full route: http://localhost:8112/api/v1/employee
    response:
        {
            "data": [
                {
                    "id": "4a3a170b-22cd-4ac2-aad1-9bb5b34a1507",
                    "employee_name": "Tiger Nixon",
                    "employee_salary": 320800,
                    "employee_age": 61,
                    "employee_title": "Vice Chair Executive Principal of Chief Operations Implementation Specialist",
                    "employee_email": "tnixon@company.com",
                },
                ....
            ],
            "status": "Successfully processed request."
        }
---
    request:
        method: GET
        path: 
            id (String)
        full route: http://localhost:8112/api/v1/employee/{id}
        note: 404-Not Found, if entity is unrecognizable
    response:
        {
            "data": {
                "id": "5255f1a5-f9f7-4be5-829a-134bde088d17",
                "employee_name": "Bill Bob",
                "employee_salary": 89750,
                "employee_age": 24,
                "employee_title": "Documentation Engineer",
                "employee_email": "billBob@company.com",
            },
            "status": ....
        }
---
    request:
        method: POST
        body: 
            name (String | not blank),
            salary (Integer | greater than zero),
            age (Integer | min = 16, max = 75),
            title (String | not blank)
        full route: http://localhost:8112/api/v1/employee
    response:
        {
            "data": {
                "id": "d005f39a-beb8-4390-afec-fd54e91d94ee",
                "employee_name": "Jill Jenkins",
                "employee_salary": 139082,
                "employee_age": 48,
                "employee_title": "Financial Advisor",
                "employee_email": "jillj@company.com",
            },
            "status": ....
        }
---
    request:
        method: DELETE
        body:
            name (String | not blank)
        full route: http://localhost:8112/api/v1/employee
    response:
        {
            "data": true,
            "status": ....
        }
