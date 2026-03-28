import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { requestOtp, verifyOtp } from "../lib/api";
import { useAuth } from "../state/AuthContext";

export default function AuthPage() {
  const navigate = useNavigate();
  const { signIn } = useAuth();
  const [step, setStep] = useState<"request" | "verify">("request");
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    name: "",
    email: "",
    otp: ""
  });

  const handleRequestOtp = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      await requestOtp(form.email, form.name);
      toast.success("OTP sent to your email.");
      setStep("verify");
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await verifyOtp(form.email, form.otp, form.name);
      signIn(response);
      toast.success("Welcome back.");
      navigate(response.role === "ADMIN" ? "/admin" : "/dashboard");
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container narrow">
      <div className="card auth-card">
        <h1>Secure login with OTP</h1>
        <p>Use one-time password login for both guest and admin access.</p>

        {step === "request" ? (
          <form className="form-stack" onSubmit={handleRequestOtp}>
            <label>
              Full name
              <input
                type="text"
                placeholder="John Doe"
                value={form.name}
                onChange={(e) => setForm((current) => ({ ...current, name: e.target.value }))}
              />
            </label>
            <label>
              Email address
              <input
                type="email"
                required
                placeholder="you@example.com"
                value={form.email}
                onChange={(e) => setForm((current) => ({ ...current, email: e.target.value }))}
              />
            </label>
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? "Sending..." : "Request OTP"}
            </button>
          </form>
        ) : (
          <form className="form-stack" onSubmit={handleVerifyOtp}>
            <label>
              Email address
              <input type="email" value={form.email} disabled />
            </label>
            <label>
              OTP code
              <input
                type="text"
                required
                placeholder="Enter 6-digit OTP"
                value={form.otp}
                onChange={(e) => setForm((current) => ({ ...current, otp: e.target.value }))}
              />
            </label>
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? "Verifying..." : "Verify OTP"}
            </button>
            <button type="button" className="secondary-button" onClick={() => setStep("request")}>
              Change email
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
