import React, { useEffect } from 'react'
import { postData } from '../../utils/api';

type Props = {}

const TrustAgentRegisterPage = (props: Props) => {

  useEffect(() => {

    postData('ta/diddoc', null)
    .then(({ url, data }) => {
      console.log('호출 성공');
    })
    .catch((err) => {
      console.error('Failed to fetch TA information:', err);
    });


  }, []);

  return (
    <div>TrustAgentRegister</div>
  )
}

export default TrustAgentRegisterPage