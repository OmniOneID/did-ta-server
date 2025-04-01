import { useEffect } from 'react';
import { useNavigate } from 'react-router';

const ApiSettingsRedirect = () => {
  const navigate = useNavigate();

  useEffect(() => {
    navigate('api-settings/expiration-settings');
  }, [navigate]);

  return null;
};

export default ApiSettingsRedirect;
