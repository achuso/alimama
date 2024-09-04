import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export function deleteTokens() {
    try {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userFullName');
        console.log("Login tokens deleted successfully!");
    }
    catch(error) {
        console.log("Error deleting login token: " + error);
    }
}

function DeleteToken({ children }) {
	const navigate = useNavigate();

	useEffect(() => {
		deleteTokens();
		navigate("/login");
	}, [navigate]);

	return children;
}

export default DeleteToken;
