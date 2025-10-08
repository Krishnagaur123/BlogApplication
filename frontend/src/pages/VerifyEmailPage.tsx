import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const VerifyEmailPage = () => {
  const [searchParams] = useSearchParams();
  const [message, setMessage] = useState('Verifying your email...');
  const navigate = useNavigate();
  const token = searchParams.get('token');

  useEffect(() => {
    const verifyEmail = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/v1/auth/verify?token=${token}`);
        setMessage('Email verified successfully! Redirecting to login...');
        setTimeout(() => navigate('/login'), 3000);
      } catch (err: any) {
        setMessage('Verification failed or link expired.');
      }
    };
    if (token) verifyEmail();
  }, [token, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full bg-white p-6 rounded shadow text-center">
        <h2 className="text-2xl font-bold mb-4">Email Verification</h2>
        <p>{message}</p>
      </div>
    </div>
  );
};

export default VerifyEmailPage;
