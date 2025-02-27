import DashboardIcon from '@mui/icons-material/Dashboard';
import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { SessionContext } from './context/SessionContext';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { getTaInfo } from './apis/TaApi';
import { getNavigationByStatus } from './config/navigationConfig';
import LoadingOverlay from './components/loading/LoadingOverlay';
import { createTheme } from '@mui/material';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, setServerStatus, setTaInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const [session, setSessionState] = useState<Session | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const setSession = useCallback((newSession: Session | null) => {
    setSessionState(newSession);
    if (newSession) {
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      localStorage.removeItem('session'); 
    }
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(() => {
    setSession(null);
    navigate('/sign-in');
  }, [navigate]);

  // Fetch TA information
  useEffect(() => {
    const fetchTaInfo = () => {
      setIsLoading(true);
      getTaInfo()
        .then(({ data }) => {
          setServerStatus(data.status);
          setTaInfo(data);
          setNavigation(getNavigationByStatus(data.status));
          setIsLoading(false);
        })
        .catch((err) => {
          navigate('/error', { state: { message: `Failed to connect server: ${err}` } });
          setIsLoading(false);
        });
    };

    fetchTaInfo();

    const handlePopState = (event: PopStateEvent) => {
      fetchTaInfo();
    };
    window.addEventListener('popstate', handlePopState);

    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  useEffect(() => {
    if (serverStatus !== null) {
      setNavigation(getNavigationByStatus(serverStatus));
    }
  }, [serverStatus]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (isLoading) {
    return <LoadingOverlay />;
  }

  let theme = createTheme({
    palette: {
      primary: {
        light: '#63ccff',
        main: '#009be5',
        dark: '#006db3',
      },
    },
    typography: {
      h5: {
        fontWeight: 500,
        fontSize: 26,
        letterSpacing: 0.5,
      },
    },
    shape: {
      borderRadius: 8,
    },
    components: {
      MuiTab: {
        defaultProps: {
          disableRipple: true,
        },
      },
    },
    mixins: {
      toolbar: {
        minHeight: 48,
      },
    },
  });
  
  theme = {
    ...theme,
    components: {
      MuiDrawer: {
        styleOverrides: {
          paper: {
            backgroundColor: '#081627',
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
          },
          contained: {
            boxShadow: 'none',
            '&:active': {
              boxShadow: 'none',
            },
          },
        },
      },
      MuiTabs: {
        styleOverrides: {
          root: {
            marginLeft: theme.spacing(1),
          },
          indicator: {
            height: 3,
            borderTopLeftRadius: 3,
            borderTopRightRadius: 3,
            backgroundColor: theme.palette.common.white,
          },
        },
      },
      MuiTab: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            margin: '0 16px',
            minWidth: 0,
            padding: 0,
            [theme.breakpoints.up('md')]: {
              padding: 0,
              minWidth: 0,
            },
          },
        },
      },
      MuiIconButton: {
        styleOverrides: {
          root: {
            color: theme.palette.common.white,
            padding: theme.spacing(1),
          },
        },
      },
      MuiTooltip: {
        styleOverrides: {
          tooltip: {
            borderRadius: 4,
          },
        },
      },
      MuiDivider: {
        styleOverrides: {
          root: {
            backgroundColor: 'rgb(255,255,255,0.15)',
          },
        },
      },
      MuiListItemButton: {
        styleOverrides: {
          root: {
            '&.Mui-selected': {
              color: '#4fc3f7',
            },
          },
        },
      },
      MuiListItemText: {
        styleOverrides: {
          primary: {
            fontSize: 14,
            fontWeight: theme.typography.fontWeightMedium,
          },
        },
      },
      MuiListItemIcon: {
        styleOverrides: {
          root: {
            color: 'inherit',
            minWidth: 'auto',
            marginRight: theme.spacing(2),
            '& svg': {
              color: '#b83b3b',
              fontSize: 20,
            },
          },
        },
      },
      MuiAvatar: {
        styleOverrides: {
          root: {
            width: 32,
            height: 32,
          },
        },
      },
      MuiSvgIcon: {
        styleOverrides: {
          root: {
            color: theme.palette.mode === 'dark' ? '#fff' : '#000', // 다크 모드일 때 흰색
          },
        },
      },      
    },
  };

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <ReactRouterAppProvider
          navigation={navigation}
          session={session}
          authentication={{ signIn, signOut }}
          // theme={theme}
        >
          <Outlet />
        </ReactRouterAppProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <ServerStatusProvider>
      <AppContent />
    </ServerStatusProvider>
  );
}
