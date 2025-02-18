import * as React from 'react';
import { SignInPage } from '@toolpad/core/SignInPage';
import type { Session } from '@toolpad/core/AppProvider';
import { useNavigate } from 'react-router';
import { useSession } from '../../context/SessionContext';

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
  return (
    <SignInPage
      providers={[{ id: 'credentials', name: 'Credentials' }]}
      signIn={async (provider, formData, callbackUrl) => {
        // Demo session
        try {
          const session = await fakeAsyncGetSession(formData);
          if (session) {
            setSession(session);
            navigate(callbackUrl || '/', { replace: true });
            return {};
          }
        } catch (error) {
          return { error: error instanceof Error ? error.message : 'An error occurred' };
        }
        return {};
      }}
    />
  );
}
