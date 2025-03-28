# TA Admin Console Source Code

Welcome to the TA Admin Console source code repository. This directory contains the frontend source code and build configurations for the TA Admin Console, developed as part of the OpenDID project.

## Directory Structure

Here's an overview of the directory structure.

```
did-ta-admin/frontend
├── public
├── src
│   ├── apis
│   ├── assets
│   ├── components
│   ├── config
│   │   └── navigationConfig.tsx
│   ├── constants
│   ├── context
│   ├── layout
│   │   └── Layout.tsx
│   ├── pages
│   │   ├── ErrorPage.tsx
│   │   ├── admins
│   │   ├── api
│   │   ├── auth
│   │   ├── dashboard
│   │   ├── entities
│   │   ├── kyc
│   │   ├── list-provider
│   │   ├── notification-provider
│   │   └── trust-agent
│   ├── utils
│   ├── App.tsx
│   ├── main.tsx
│   ├── theme.ts
│   └── vite-env.d.ts
├── index.html
├── package.json
├── vite.config.ts
```

Below is a description of each folder and file in the `src` directory:

| Name                    | Description                                              |
| ----------------------- | -------------------------------------------------------- |
| public                  | Static assets served as-is from the root path `/`        |
| src/apis                | API client modules for TA Admin features                 |
| src/assets              | Static assets used in the application                    |
| src/components          | Reusable UI components                                   |
| src/config              | Application configuration settings                       |
| ┖ navigationConfig.tsx  | Sidebar navigation configuration                         |
| src/constants           | Constant values and enums used across the app            |
| src/context             | React context for global state management                |
| src/layout              | Layout components including topbar and sidebar           |
| ┖ Layout.tsx            | Main layout component                                    |
| src/pages               | Page-level components mapped to route segments           |
| ┖ ErrorPage.tsx         | Error fallback page                                      |
| ┖ admins                | Pages related to the Admin Management menu               |
| ┖ api                   | Pages related to the API Settings menu                   |
| ┖ auth                  | Pages related to login and authentication                |
| ┖ dashboard             | Landing page after login                                 |
| ┖ entities              | Pages related to the Entity Management menu              |
| ┖ kyc                   | Pages related to the KYC Settings menu                   |
| ┖ list-provider         | Pages related to the List Provider Settings menu         |
| ┖ notification-provider | Pages related to the Notification Provider Settings menu |
| ┖ trust-agent           | Pages related to the TA Registration / Management menus  |
| src/utils               | Utility functions and helpers                            |
| src/App.tsx             | Root component of the React application                  |
| src/main.tsx            | Application bootstrap and mount logic                    |
| src/theme.ts            | MUI theme configuration                                  |
| src/vite-env.d.ts       | Type definitions for Vite environment                    |

## Libraries

Libraries used in this project are third-party open-source dependencies managed via the [package.json](./package.json) file. Major frameworks and tools include:

- `React` (v19)
- `Vite` (v5)
- `TypeScript`
- `MUI` (Material UI v6, including X-DataGrid and X-Charts)
- `Styled-Components`
- `React Router v7`
- `@emotion/react`, `@emotion/styled`
- `core-vite-auth`: Authentication utility package for Vite-based applications

For a detailed list of third-party libraries and their licenses, please refer to the [dependencies-license.md](../../../dependencies-license.md) file.

## Documentation

Refer to the following documents for more detailed information:
- [OpenDID TA Admin Console Operation Guide](../../../docs/admin/OpenDID_TAAdmin_Operation_Guide_ko.md)

## Contributing

Please read [CONTRIBUTING.md](../../../CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](../../../CODE_OF_CONDUCT.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the [Apache 2.0](../../../LICENSE) license.

## Contact

For questions or support, please contact [maintainers](../../../MAINTAINERS.md).
