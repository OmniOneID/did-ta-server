import { CssBaseline, GlobalStyles, useTheme } from '@mui/material';
import { PasswordPolicyProvider } from './context/PasswordPolicyContext';
import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { tokenStore } from './utils/authTokens';
import { restoreSessionFromToken } from './utils/authSync';
import { ADMIN_LOGOUT_EVENT, logoutAdmin } from './utils/logout';
import { useIdleLogout } from './hooks/useIdleLogout';
import { getTaInfo } from './apis/ta-api';
import LoadingOverlay from './components/loading/LoadingOverlay';
import { getNavigationByStatus } from './config/navigationConfig';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { ExtendedSession, SessionContext } from './context/SessionContext';
import customTheme from './theme';
import { formatErrorMessage } from './utils/error-handler';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, setServerStatus, setTaInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const [session, setSessionState] = useState<ExtendedSession | null>(() => {
    const storedSession = sessionStorage.getItem('session') ?? localStorage.getItem('session');
    return storedSession && tokenStore.getRefresh() ? JSON.parse(storedSession) : null;
  });
  const [isRestoring, setIsRestoring] = useState(() => !session && !!tokenStore.getRefresh());

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const theme = useTheme();

  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      sessionStorage.setItem('session', JSON.stringify(newSession));
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      sessionStorage.removeItem('session');
      localStorage.removeItem('session');
    }
  }, []);

  useEffect(() => {
    if (session || !tokenStore.getRefresh()) return;
    void restoreSessionFromToken('/tas/admin/v1')
      .then((restored) => { if (restored) setSession(restored); })
      .finally(() => setIsRestoring(false));
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(async () => {
    if (tokenStore.isPendingLogout()) {
      sessionStorage.removeItem('session');
      sessionStorage.removeItem('activeSession');
      window.location.replace('/sign-in');
      return;
    }
    await logoutAdmin('/tas/admin/v1');
    setSession(null);
    navigate('/sign-in');
  }, [navigate, setSession]);

  useEffect(() => {
    const onStorage = (event: StorageEvent) => {
      if (event.key === ADMIN_LOGOUT_EVENT && event.newValue) {
        tokenStore.markRemoteLogout(event.newValue);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [navigate, setSession]);

  // Fetch TA information
  useEffect(() => {
    if (!session) {
      setIsLoading(false);
      return;
    }
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
          navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Trust Agent Information") } });
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
  }, [session, navigate]);

  useEffect(() => {
    if (serverStatus !== null) {
      setNavigation(getNavigationByStatus(serverStatus));
    }
  }, [serverStatus]);

  useIdleLogout(session !== null, signOut);

  const displaySession = useMemo(() => session ? {
    ...session,
    user: {
      ...session.user,
      name: session.user?.name || session.user?.id || 'Admin',
      email: session.user?.email || session.user?.id || undefined,
    },
  } : null, [session]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (isLoading || isRestoring) {
    return <LoadingOverlay />;
  }

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <ReactRouterAppProvider
          navigation={navigation}
          session={displaySession}
          authentication={{ signIn, signOut }}
          theme={customTheme}
        >
          <CssBaseline />
          <Outlet />
        </ReactRouterAppProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <PasswordPolicyProvider>
      <ServerStatusProvider>
        <GlobalStyles styles={{ body: { padding: "10px" } }} />
        <AppContent />
      </ServerStatusProvider>
    </PasswordPolicyProvider>
  );
}
