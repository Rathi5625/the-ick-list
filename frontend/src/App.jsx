import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import UploadPage from './pages/UploadPage';
import RoastResultsPage from './pages/RoastResultsPage';
import RoastHistoryPage from './pages/RoastHistoryPage';

function AppContent() {
  const { isAuthenticated } = useAuth();
  const [theme, setTheme] = useState('dark');
  const [currentView, setCurrentView] = useState('signup'); // 'signup', 'login', 'upload', 'results', 'history'
  const [currentRoasts, setCurrentRoasts] = useState([]);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  useEffect(() => {
    if (isAuthenticated) {
      if (currentView === 'signup' || currentView === 'login') {
        setCurrentView('upload');
      }
    } else if (currentView === 'upload' || currentView === 'results' || currentView === 'history') {
      setCurrentView('login');
    }
  }, [isAuthenticated]);

  const toggleTheme = () => {
    setTheme(prev => (prev === 'dark' ? 'light' : 'dark'));
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar
        theme={theme}
        onToggleTheme={toggleTheme}
        currentView={currentView}
        onNavigate={setCurrentView}
      />

      <main style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '32px 16px' }}>
        {/* Unauthenticated Views */}
        {!isAuthenticated && (
          <div style={{ width: '100%', maxWidth: '480px' }}>
            {currentView === 'signup' && (
              <SignupPage
                onNavigateToLogin={() => setCurrentView('login')}
                onSignupSuccess={() => setCurrentView('upload')}
              />
            )}

            {currentView === 'login' && (
              <LoginPage
                onNavigateToSignup={() => setCurrentView('signup')}
                onLoginSuccess={() => setCurrentView('upload')}
              />
            )}
          </div>
        )}

        {/* Authenticated Views */}
        {isAuthenticated && (
          <>
            {currentView === 'upload' && (
              <UploadPage onRoastsGenerated={(roasts) => {
                setCurrentRoasts(roasts);
                setCurrentView('results');
              }} />
            )}

            {currentView === 'results' && (
              <RoastResultsPage
                roasts={currentRoasts}
                onUploadAgain={() => setCurrentView('upload')}
                onNavigateToHistory={() => setCurrentView('history')}
              />
            )}

            {currentView === 'history' && (
              <RoastHistoryPage onNavigateToUpload={() => setCurrentView('upload')} />
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
