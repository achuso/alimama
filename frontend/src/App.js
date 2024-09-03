import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import AuthContainer from './components/WelcomePage/AuthContainer.tsx';
import NotFound from './components/NotFound.tsx';
import PrivateRoute from './components/PrivateRoute.tsx'; 
import VendorDashboard from './components/VendorPage/VendorDashboard.tsx'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AuthContainer />} />
        <Route path="/login" element={<AuthContainer />} />
        <Route path="/vendor-dashboard" element={<PrivateRoute><VendorDashboard /></PrivateRoute>} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
