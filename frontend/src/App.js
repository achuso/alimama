import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import AuthContainer from './components/WelcomePage/AuthContainer.tsx';
import NotFound from './components/NotFound.tsx';
import PrivateRoute from './components/PrivateRoute.tsx'; 
import DeleteToken from './components/DeleteToken.tsx';

import VendorDashboard from './components/VendorPage/VendorDashboard.tsx'
import CustomerDashboard from './components/CustomerPage/CustomerDashboard.tsx';
import ItemDetails from './components/CustomerPage/ItemDetails.tsx';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AuthContainer />} />
        <Route path="/login" element={<AuthContainer />} />
        <Route path="/vendor-dashboard" element={<PrivateRoute><VendorDashboard /></PrivateRoute>} />
        <Route path="/customer-dashboard" element={<PrivateRoute><CustomerDashboard/></PrivateRoute>} />
        <Route path="/item/:id" element={<PrivateRoute><ItemDetails/></PrivateRoute>}/>
        <Route path="*" element={<NotFound />} />
        <Route path="/logout" element={<DeleteToken><AuthContainer/></DeleteToken>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
