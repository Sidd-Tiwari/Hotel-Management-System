import { Hotel, LogOut, Shield, User } from "lucide-react";
import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../state/AuthContext";

export function Navbar() {
  const { user, signOut } = useAuth();

  return (
    <header className="navbar-shell">
      <div className="container navbar">
        <Link className="brand" to="/">
          <Hotel size={20} />
          <span>HotelHub</span>
        </Link>

        <nav className="nav-links">
          <NavLink to="/">Home</NavLink>
          <NavLink to="/rooms">Rooms</NavLink>
          {user && <NavLink to="/dashboard">Dashboard</NavLink>}
          {user?.role === "ADMIN" && <NavLink to="/admin">Admin</NavLink>}
        </nav>

        <div className="nav-actions">
          {user ? (
            <>
              <span className="chip">
                {user.role === "ADMIN" ? <Shield size={16} /> : <User size={16} />}
                {user.name}
              </span>
              <button className="ghost-button" onClick={signOut}>
                <LogOut size={16} />
                Logout
              </button>
            </>
          ) : (
            <Link className="primary-button" to="/auth">
              Login / Signup
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
