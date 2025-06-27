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
import TrustAgentRegistrationPageSimple from './pages/trust-agent/TrustAgentRegistrationSimplePage';
import PushServerSettingsPage from './pages/notification-provider/push-server/PushServerSettingsPage';
import AllowedCaManagementPage from './pages/list-provider/allowed-ca/AllowedCaManagementPage';
import ListProviderPage from './pages/list-provider/ListProviderPage';
import AllowedCaRegistrationPage from './pages/list-provider/allowed-ca/AllowedCaRegistrationPage';
import AllowedCaEditPage from './pages/list-provider/allowed-ca/AllowedCaEditPage';
import AllowedCaDetailPage from './pages/list-provider/allowed-ca/AllowedCaDetailPage';
import VcSchemaManagementPage from './pages/list-provider/vc-schema/VcSchemaManagementPage';
import VcSchemaDetailPage from './pages/list-provider/vc-schema/VcSchemaDetailPage';
import VcPlanManagementPage from './pages/list-provider/vc-plan/VcPlanManagementPage';
import VcPlanDetailPage from './pages/list-provider/vc-plan/VcPlanDetailPage';
import AdminDetailPage from './pages/admins/AdminDetailPage';
import AdminRegisterPage from './pages/admins/AdminRegisterPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import TrustAgentRegistrationPage from './pages/trust-agent/TrustAgentRegistrationPage';
import UserListPage from './pages/user/user-list/UserListPage';
import AppListPage from './pages/user/app-list/AppListPage';
import WalletListPage from './pages/user/wallet-list/WalletListPage';
import UserDetailPage from './pages/user/user-list/UserDetailPage';
import AppDetailPage from './pages/user/app-list/AppDetailPage';
import WalletDetailPage from './pages/user/wallet-list/WalletDetailPage';
import CredentialSchemaDetailPage from './pages/list-provider/credential-schema/CredentialSchemaDetailPage';
import CredentialSchemaManagementPage from './pages/list-provider/credential-schema/CredentialSchemaManagementPage';
import CredentialDefinitionDetailPage from './pages/list-provider/credential-definition/CredentialDefinitionDetailPage';
import CredentialDefinitionManagementPage from './pages/list-provider/credential-definition/CredentialDefinitionManagementPage';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '/',
            Component: DashboardPage
          },
          {
            path: '/ta-registration',
            Component: TrustAgentRegistrationPage,
          },
          {
            path: '/ta-management',
            Component: TrustAgentManagementPage,
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
            path: '/entities/entity-management',
            Component: EntityManagementPage,
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
            path: 'noti-settings/push-server',
            Component: PushServerSettingsPage,
          },
          {
            path: 'noti-settings',
            Component: NotificationProviderPage,
          },
          {
            path: 'list-settings/allowed-ca/allowed-ca-registration',
            Component: AllowedCaRegistrationPage,
          },
          {
            path: 'list-settings/allowed-ca/allowed-ca-edit/:id',
            Component: AllowedCaEditPage
          },
          {
            path: 'list-settings/allowed-ca/:id',
            Component: AllowedCaDetailPage
          },
          {
            path: 'list-settings/allowed-ca',
            Component: AllowedCaManagementPage,
          },
          {
            path: 'list-settings',
            Component: ListProviderPage,
          },
          {
            path: 'list-settings/vc-schema/:id',
            Component: VcSchemaDetailPage
          },
          {
            path: 'list-settings/vc-schema',
            Component: VcSchemaManagementPage,
          },
          {
            path: 'list-settings/vc-plan/:id',
            Component: VcPlanDetailPage,
          },
          {
            path: 'list-settings/vc-plan',
            Component: VcPlanManagementPage,
          },
          {
            path: 'list-settings/credential-schema/:id',
            Component: CredentialSchemaDetailPage
          },
          {
            path: 'list-settings/credential-schema',
            Component: CredentialSchemaManagementPage,
          },
          {
            path: 'list-settings/credential-definition/:id',
            Component: CredentialDefinitionDetailPage
          },
          {
            path: 'list-settings/credential-definition',
            Component: CredentialDefinitionManagementPage,
          },
          {
            path: 'admin-management/admin-registration',
            Component: AdminRegisterPage,
          },
          {
            path: 'admin-management/:id',
            Component: AdminDetailPage,
          },
          {
            path: 'admin-management',
            Component: AdminManagementPage,
          },
          {
            path: 'user-management/user-list/:id',
            Component: UserDetailPage,
          },
          {
            path: 'user-management/user-list',
            Component: UserListPage,
          },
          {
            path: 'user-management/app-list/:id',
            Component: AppDetailPage,
          },
          {
            path: 'user-management/app-list',
            Component: AppListPage,
          },
          {
            path: 'user-management/wallet-list/:id',
            Component: WalletDetailPage,
          },
          {
            path: 'user-management/wallet-list',
            Component: WalletListPage,
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
