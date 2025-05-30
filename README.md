TA(Trust Agent) Server
==

Welcome to the TA Server Repository. <br>
This repository contains the source code, documentation, and related resources for the TAS Server.

## S/W Specifications
| Category           | Details                                         |
|--------------------|-------------------------------------------------|
| OS                 | macOS / Linux / Windows 10 or higher            |
| Language           | Java 21 or higher                               |
| IDE                | IntelliJ IDEA                                   |
| Build System       | Gradle 7.0 or higher                            |
| Compatibility      | Requires JDK 21 or higher                       |
| Docker             | Docker and Docker Compose must be installed     |
| Other Requirements | Minimum 2GB RAM and 10GB of disk space required |

## Folder Structure
Overview of the major folders and documents in the project directory:

```
did-ta-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   └── admin
│       ├── OpenDID_TAAdmin_Operation_Guide.md
│       └── OpenDID_TAAdmin_Operation_Guide_ko.md
│   └── api
│       ├── TAS_API.md
│       └── TAS_API_ko.md
│   └── errorCode
│       ├── TAS_ErrorCode.md
│       └── TAS_ErrorCode_ko.md
│   └── installation
│       ├── OpenDID_TASServer_InstallationAndOperation_Guide.md
│       └── OpenDID_TASServer_InstallationAndOperation_Guide_ko.md
│   └── db
│       ├── OpenDID_TableDefinition_TAS.md
│       └── OpenDID_TableDefinition_TAS_ko.md
└── source
    └── did-ta-admin
        ├── frontend
    └── did-ta-server
```

<br/>

Below is a description of each folder and file in the directory:

| Name                         | Description                                     |
| ---------------------------- | ----------------------------------------------- |
| CHANGELOG.md                 | Version-specific changes in the project         |
| CODE_OF_CONDUCT.md           | Code of conduct for contributors                |
| CONTRIBUTING.md              | Contribution guidelines and procedures          |
| LICENSE                      | License                                         |
| dependencies-license.md      | Licenses for the project’s dependency libraries |
| MAINTAINERS.md               | Guidelines for project maintainers              |
| RELEASE-PROCESS.md           | Procedures for releasing new versions           |
| SECURITY.md                  | Security policies and vulnerability reporting   |
| docs                         | Documentation                                   |
| ┖ admin                      | Admin Console guide documentation              |
| ┖ api                        | API guide documentation                         |
| ┖ errorCode                  | Error codes and troubleshooting guides          |
| ┖ installation               | Installation and setup instructions             |
| ┖ db                         | Database ERD,  Table Specifications             |
| source                       | Source code for the server and admin console    |
| ┖ did-ta-server              | TA Server source code                           |
| ┖ did-ta-admin               | TA Admin source code                            |
| &nbsp;&nbsp;&nbsp;┖ frontend | TA Admin frontend source code                   |

<br/>

## Installation And Operation Guide

For detailed instructions on installing the TA Server, please refer to the guide below:
- [OpenDID TA Server Installation Guide](docs/installation/OpenDID_TAServer_Installation_Guide.md)  

For detailed instructions on operating the TA Admin Console, please refer to the guide below:  
- [OpenDID TA Admin Console Operation Guide](docs/admin/OpenDID_TAAdmin_Operation_Guide_ko.md)

## API Reference

- **TA API**: Detailed reference for the TA Server's API endpoints and usage.
  - [TA API Reference](docs/api/TAS_API_ko.md)

## Change Log

The Change Log provides a detailed record of version-specific changes and updates. You can find it here:
- [Change Log](./CHANGELOG.md)  

## OpenDID Demonstration Videos <br>
To watch our demonstration videos of the OpenDID system in action, please visit our [Demo Repository](https://github.com/OmniOneID/did-demo-server). <br>

These videos showcase key features including user registration, VC issuance, and VP submission processes.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
[Apache 2.0](LICENSE)
