import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import SignInPage from './pages/auth/SignIn';
import TrustAgentRegistrationPage from './pages/trust-agent/TrustAgentRegistrationPage';
import ErrorPage from './pages/ErrorPage';
import TrustAgentManagementPage from './pages/trust-agent/TrustAgentManagementPage';

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
