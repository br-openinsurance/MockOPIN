# insurance-server-lambdas

This project is a Micronaut-based resource server designed to serve mock data for Open Insurance APIs. It uses a PostgreSQL database to store the data and its componentes are containerized through docker.

It also depends on two extra repositories:

- [insurance-swagger](https://github.com/raidiam/insurance-swagger): Responsible for managing the swagger file and generating all required models and clients.

- [mock-service-os](https://github.com/raidiam/mock-service-os): Responsible for providing a complete local instance of Mock mTLS gateway and OpenID Provider, including all components needed to adhere to FAPI standards.

## Setup

To begin setting up the project, simply run `make setup`. This will run the setup for both insurance-swagger and mock-service-os and build the containers for auth and mtls, which can be run separetely through the commands `make swagger`, `make mock-service-os` and `make build-auth-mtls`, respectively.

`make swagger` will generate the models and clients, while `make mock-service-os` (or `make certs`) will generate the certificates needed.

Also, on the first time only, run `make hosts` to add the needed hosts to /etc/hosts.

### Setting up Postman

1. Download and install [Postman](https://www.postman.com/downloads/)
2. Install [postman util lib](https://joolfe.github.io/postman-util-lib/)
3. Import [mock collection](/postman/collection.json)
4. Import [mock environment](/postman/environment.json)
5. Add certificates to postman
    1. Open Postman
    2. Go to Settings -> Certificates
    3. Add the CRT and KEY files for the client in `mock-service-os/certs/` for both `api.local` and `auth.local` hosts

### Setting up the Conformance Suite

1. Run `make setup-cs`
2. Copy the contents of the `cs_config.json` file

## Running

### Locally through Postman

After setting up Postman, run `make run` to start the application. You can then make requests to `https://auth.local` and `https://api.local`.

You'll first have to make the request for the Token which will automatically save it to a variable to be used on every other request. You can then use the requests available or create a new one if developing a new API.

Apart from the endpoints in the collection, you can check what other endpoints are available through the [participants.json file](participants.json) and what scopes are available by making a GET request to `https://auth.local/.well-known/openid-configuration`.

### Locally through the Conformance Suite

After setting up the Conformance Suite, run `make run-with-cs`to start the application, which will be available on `https://localhost:8443/`.

You'll first have to create a new test plan and select the test plan for the API you're trying to test. Then, you'll have paste the contents of the `cs_config.json` file in the JSON tab, complete any needed information on the Form tab and click on the `Create Test Plan` button.

## Contributing

### Folder structure

    .
    ├── postman                                                     # Postman files (collection and environment)
    ├── src                                                         # Source files
    │   ├── main                                                    
    │   │   ├── ...    
    │   │   │   ├── auth                                            
    │   │   │   │   └── SimpleAuthorisation.java                    # Mapping of scopes to roles
    │   │   │   ├── controllers                                     
    │   │   │   ├── db                                              # DB Initializer
    │   │   │   ├── domain                                          
    │   │   │   ├── exceptions                                      
    │   │   │   ├── fapi                                            
    │   │   │   ├── handlers                                        
    │   │   │   ├── repository                                      
    │   │   │   ├── services                                        
    │   │   │   ├── utils                                           
    │   │   │   └── SwaggerIntrospectionConfig.java                 # Configuration for instrospecting swagger generated classes
    │   │   └── resources/db                                        # DB migration and dataloading scripts
    │   │       ├── dataloading                                     
    │   │       └── migration                                       
    │   └── test                                                    # Automated tests
    │       └── ...
    │           ├── controllers                                     
    │           ├── services                                        
    │           ├── utils                                           
    │           ├── CleanupSpecification.groovy                     # Repository injection and cleanup for tests
    │           ├── TestEntityDataFactory.groovy                    # Generation of entities for the tests
    │           └── TestRequestDataFactory.groovy                   # Generation of requests for the tests
    ├── participants.json                                           # Configuration file to mock endpoints
    ├── build.gradle                                                
    ├── Makefile                                                    
    └── README.md                                                   

### Developing a new API

#### In insurance-swagger repo

- Update the swagger with the required paths and objects

#### In insurance-server-lambdas repo

- Run `make swagger` to build it on a new version
- Update swaggerVersion in `build.gradle`
- Add new endpoints to `participants.json`
- Add scopes to `SimpleAuthorisation.java`
- Create Controllers, Entities, Services and Repositories
- Add Request and Response classes to `SwaggerIntrospectionConfig.java`
- Inject the repositories in `BaseInsuranceService.java`
- Create the table through an SQL script in `src/resources/db/migration`
- Add methods for the created entities in `TestEntityDataFactory.groovy`
- Add methods for the needed requests in `TestRequestDataFactory.groovy`
- Create Controller and Service tests
- Inject the repositories in `CleanupSpecification.groovy` and add them to the cleanup process

#### In mock-service-os

- Add scopes to `mock_as/utils/opin/configuration.js`

### Branch rules

All changes need to follow this workflow.

Any published branches and pull requests will all be built with Jenkins and all PRs must include the Jira ticket ID relating to the change, for example: `RPB-123 adding new resource`

The following repository rules are enforced:

- **Pushing to main is disallowed**
- **Pull requests must be up to date with master**
- **Pull requests require one approver**
- **Pull requests require successful build**
