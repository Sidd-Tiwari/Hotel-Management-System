import { Link } from "react-router-dom";
import type { Room } from "../types";

export function RoomCard({ room }: { room: Room }) {
  return (
    <article className="room-card">
      <img
        src={room.imageUrl || "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=1200"}
        alt={room.name}
        className="room-image"
      />
      <div className="room-content">
        <div className="row-between">
          <span className="badge">{room.type}</span>
          <strong>₹{room.pricePerNight}/night</strong>
        </div>
        <h3>{room.name}</h3>
        <p>{room.description}</p>
        <div className="meta-row">
          <span>Room #{room.roomNumber}</span>
          <span>Up to {room.capacity} guests</span>
        </div>
        <div className="row-between">
          <span className={room.active ? "text-success" : "text-danger"}>
            {room.active ? "Available" : "Unavailable"}
          </span>
          <Link className="primary-button" to={`/rooms/${room.id}`}>
            View details
          </Link>
        </div>
      </div>
    </article>
  );
}
