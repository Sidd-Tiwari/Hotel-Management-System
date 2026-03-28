import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { api } from "../lib/api";
import type { Room } from "../types";
import { LoadingSpinner } from "../components/LoadingSpinner";

export default function RoomDetailsPage() {
  const { roomId } = useParams();
  const [room, setRoom] = useState<Room | null>(null);

  useEffect(() => {
    if (!roomId) return;
    api<Room>(`/api/public/rooms/${roomId}`)
      .then(setRoom)
      .catch((error: Error) => toast.error(error.message));
  }, [roomId]);

  if (!room) return <LoadingSpinner label="Loading room details..." />;

  return (
    <div className="container page-stack">
      <div className="details-grid">
        <img
          src={room.imageUrl || "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=1200"}
          alt={room.name}
          className="details-image"
        />
        <div className="card details-panel">
          <span className="badge">{room.type}</span>
          <h1>{room.name}</h1>
          <p>{room.description}</p>
          <div className="details-stats">
            <div>
              <strong>₹{room.pricePerNight}</strong>
              <span>Per night</span>
            </div>
            <div>
              <strong>{room.capacity}</strong>
              <span>Max guests</span>
            </div>
            <div>
              <strong>#{room.roomNumber}</strong>
              <span>Room number</span>
            </div>
          </div>
          <div className="amenity-list">
            {room.amenities.split(",").map((item) => (
              <span key={item} className="badge soft">
                {item}
              </span>
            ))}
          </div>
          <Link className="primary-button full-width" to={`/book/${room.id}`}>
            Book this room
          </Link>
        </div>
      </div>
    </div>
  );
}
