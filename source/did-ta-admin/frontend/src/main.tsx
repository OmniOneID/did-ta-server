import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import SignInPage from './pages/auth/SignIn';
import EntityManagementPage from './pages/entities/EntityManagementPage';
import ErrorPage from './pages/ErrorPage';
import TrustAgentManagementPage from './pages/trust-agent/TrustAgentManagementPage';
import TrustAgentRegistrationPage from './pages/trust-agent/TrustAgentRegistrationPage';
import EntityDetailPage from './pages/entities/EntityDetailPage';
import EntityRegistrationPage from './pages/entities/EntityRegistrationPage';
import KycSettingPage from './pages/kyc/KycSettingPage';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '/ta-registration',
            Component: TrustAgentRegistrationPage,
          },
          {
            path: '/ta-management',
            Component: TrustAgentManagementPage,
          },
          {
            path: '/entities/entity-management',
            Component: EntityManagementPage,
          },
          {
            path: '/entities/entity-management/:entityId',
            Component: EntityDetailPage
          },
          {
            path: '/entities/entity-registration',
            Component: EntityRegistrationPage
          },
          {
            path: '/kyc/kyc-settings',
            Component: KycSettingPage
          }
        ],
      },
      {
        path: '/sign-in',
        Component: SignInPage,
      },
      {
        path: '/error',
        Component: ErrorPage,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);
