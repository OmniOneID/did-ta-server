import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';


export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'COMPLETED') {
    return [
      {
        kind: 'divider',
      },
      { 
        segment: 'ta-registration', title: 'TA Registration'
      },
      {
        kind: 'divider',
      },
    ];
  } 
  return [
    {
      kind: 'divider',
    },
    { 
      segment: 'ta-management', 
      title: 'TA Management', 
    },
    {
      segment: 'entities/entity-management',
      title: 'Entity Management',
    },
    {
      segment: 'kyc/kyc-settings',
      title: 'KYC Settings',
    },
    {
      segment: 'api-settings',
      title: 'API Settings',
      children: [
        {
          segment: 'expiration-settings',
          title: 'Expiration Settings',
        },
        {
          segment: 'key-exchange-policy',
          title: 'Key Exchange Policy',
        },
      ]
    },
    {
      segment: 'noti-settings',
      title: 'Notification Provider Settings',
      children: [
        {
          segment: 'email-server',
          title: 'Email Server Settings',
        },
        {
          segment: 'email-template',
          title: 'Email Template Settings',
        },
        {
          segment: 'push-server',
          title: 'Push Server Settings',
        },
      ]
    },
    {
      segment: 'list-settings',
      title: 'List Provider Settings',
      children: [
        {
          segment: 'allowed-ca',
          title: 'Allowed CA Management',
        },
        {
          segment: 'vc-schema',
          title: 'VC Schema Management',
        },
        {
          segment: 'vc-plan',
          title: 'VC Plan Management',
        },
        {
          segment: 'credential-schema',
          title: 'Credential Schema Management',
        },
        {
          segment: 'credential-definition',
          title: 'Credential Definition Management',
        },
      ],
    },
    {
      segment: 'user-management',
      title: 'User Management',
      children: [
        {
          segment: 'user-list',
          title: 'User List',
        },
        {
          segment: 'app-list',
          title: 'App List',
        },
        {
          segment: 'wallet-list',
          title: 'Wallet List',
        },
      ],
    },
    {
      segment: 'admin-management',
      title: 'Admin Management', 
    },
    {
      kind: 'divider',
    },
  ];
};
