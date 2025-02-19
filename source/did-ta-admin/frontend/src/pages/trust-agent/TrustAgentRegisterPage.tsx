import { postData } from '../../utils/api';

type Props = {}

const TrustAgentRegisterPage = (props: Props) => {

  const handlePostData = () => {
    postData('ta/register-simple', null)
      .then(({ url, data }) => {
        console.log('호출 성공');
      })
      .catch((err) => {
        console.error('Failed to fetch TA information:', err);
      });
  };

  return (
    <div>
      <h2>TrustAgent Register</h2>
      <button onClick={handlePostData}>TA 등록 요청</button>
    </div>
  );
}

export default TrustAgentRegisterPage;
