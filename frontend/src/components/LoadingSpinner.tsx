export function LoadingSpinner({ label = "Loading..." }: { label?: string }) {
  return (
    <div className="center-card">
      <div className="spinner" />
      <p>{label}</p>
    </div>
  );
}
