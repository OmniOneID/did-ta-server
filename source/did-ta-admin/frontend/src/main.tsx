import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import AdminManagementPage from './pages/admins/AdminManagementPage';
import ApiSettingsRedirect from './pages/api/ApiSettingsPage';
import KeyExchangePolicyPage from './pages/api/key-exchange-policy/KeyExchangePolicyPage';
import ExpirationSettingsPage from './pages/api/token-expiry/ExpirationSettingsPage';
import SignInPage from './pages/auth/SignIn';
import EntityDetailPage from './pages/entities/EntityDetailPage';
import EntityManagementPage from './pages/entities/EntityManagementPage';
import EntityRegistrationPage from './pages/entities/EntityRegistrationPage';
import ErrorPage from './pages/ErrorPage';
import KycSettingPage from './pages/kyc/KycSettingPage';
import EmailServerSettingsPage from './pages/notification-provider/email-server/EmailServerSettingsPage';
import EmailTemplateSettingsPage from './pages/notification-provider/email-template/EmailTemplateSettingsPage';
import NotificationProviderPage from './pages/notification-provider/NotificationProviderPage';
import TrustAgentManagementPage from './pages/trust-agent/TrustAgentManagementPage';
import TrustAgentRegistrationPage from './pages/trust-agent/TrustAgentRegistrationPage';

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
          },
          {
            path: '/api-settings/expiration-settings',
            Component: ExpirationSettingsPage,
          },
          {
            path: '/api-settings/key-exchange-policy',
            Component: KeyExchangePolicyPage,
          },
          {
            path: '/api-settings',
            Component: ApiSettingsRedirect,
          },
          {
            path: 'noti-settings/email-server',
            Component: EmailServerSettingsPage,
          },
          {
            path: 'noti-settings/email-server',
            Component: EmailServerSettingsPage,
          },
          {
            path: 'noti-settings/email-template',
            Component: EmailTemplateSettingsPage,
          },
          {
            path: 'noti-settings',
            Component: NotificationProviderPage,
          },
          {
            path: 'admin-management',
            Component: AdminManagementPage,
          },
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
