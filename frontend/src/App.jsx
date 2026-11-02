import { useEffect } from "react";
import { Routes, Route, Navigate, Outlet, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import WhatsAppButton from "./components/WhatsAppButton";
import Accueil from "./pages/Accueil";
import ReserverCaravane from "./pages/ReserverCaravane";
import AdminAuth from "./pages/AdminAuth";
import AdminLayout from "./components/AdminLayout";
import AdminGuard from "./components/AdminGuard";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminCaravanForm from "./pages/admin/AdminCaravanForm";
import AdminCaravanList from "./pages/admin/AdminCaravanList";
import AdminCaravanDetail from "./pages/admin/AdminCaravanDetail";
import AdminProfile from "./pages/admin/AdminProfile";
import AdminPasswordChange from "./pages/admin/AdminPasswordChange";
import { isAuthenticated } from "./api/admin";

function AdminEntry() {
  return isAuthenticated()
    ? <Navigate to="/admin/tableau-de-bord" replace />
    : <AdminAuth />;
}
function ScrollToHash() {
  const { pathname, hash } = useLocation();
  useEffect(() => {
    if (hash) document.getElementById(hash.slice(1))?.scrollIntoView({ behavior: "smooth" });
    else window.scrollTo(0, 0);
  }, [pathname, hash]);
  return null;
}

export default function App() {
  const { pathname } = useLocation();
  const isAdmin = pathname.startsWith("/admin");
  useEffect(() => {
    document.body.classList.toggle("admin-body", isAdmin);
    return () => document.body.classList.remove("admin-body");
  }, [isAdmin]);

  return (
    <>
      <ScrollToHash />
      {!isAdmin && <Navbar />}
      <main>
        <Routes>
          <Route path="/" element={<Accueil />} />
          <Route path="/a-propos" element={<Navigate to="/#a-propos" replace />} />
          <Route path="/reserver/:id" element={<ReserverCaravane />} />
          <Route path="/admin" element={<Outlet />}>
            <Route index element={<AdminEntry />} />
            <Route path="mot-de-passe-oublie" element={<AdminAuth />} />
            <Route element={<AdminGuard><AdminLayout /></AdminGuard>}>
              <Route path="tableau-de-bord" element={<AdminDashboard />} />
              <Route path="caravanes/nouvelle" element={<AdminCaravanForm />} />
              <Route path="caravanes" element={<AdminCaravanList />} />
              <Route path="caravanes/:id" element={<AdminCaravanDetail />} />
              <Route path="caravanes/:id/modifier" element={<AdminCaravanForm />} />
              <Route path="historique" element={<AdminCaravanList history />} />
              <Route path="profil" element={<AdminProfile />} />
              <Route path="profil/mot-de-passe" element={<AdminPasswordChange />} />
            </Route>
            <Route path="*" element={<Navigate to="/admin" replace />} />
          </Route>
        </Routes>
      </main>
      {!isAdmin && <Footer />}
      {!isAdmin && <WhatsAppButton />}
    </>
  );
}
