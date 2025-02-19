import * as React from 'react';
import { SignInPage } from '@toolpad/core/SignInPage';
import type { Session } from '@toolpad/core/AppProvider';
import { useNavigate } from 'react-router';
import { useSession } from '../../context/SessionContext';
import { useServerStatus } from '../../context/ServerStatusContext';
import { helthCheck } from '../../apis/TaApi';

const fakeAsyncGetSession = async (formData: any): Promise<Session> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (formData.get('password') === 'password') {
        resolve({
          user: {
            name: formData.get('email'),
          },
        });
      }
      reject(new Error('Incorrect credentials.'));
    }, 1000);
  });
};

export default function SignIn() {
  const { setSession } = useSession();
  const navigate = useNavigate();

  const { serverStatus } = useServerStatus();

  return (
    <SignInPage
      providers={[{ id: 'credentials', name: 'Credentials' }]}
      signIn={async (provider, formData, callbackUrl) => {

        try {
            // TODO: Until the login API is developed, replace the login with a temporary health check API.
            const session = {
              user: { name: formData.get('email') },
            };
            setSession(session);

            helthCheck()
            .then(({ url, data }) => {
              if (serverStatus !== 'COMPLETED') {
                navigate('/ta-register', { replace: true });
              } else {
                // TODO: Modify when the TA management page is developed
                navigate('/ta-register', { replace: true });
              }
            })
            .catch((err) => {
              console.error('Failed to fetch TA information:', err);
              navigate('/error');
              setSession(null);
            });

            return {};
        } catch (error) {
          return { error: error instanceof Error ? error.message : 'An error occurred' };
        }
      }}
    />
  );
}
