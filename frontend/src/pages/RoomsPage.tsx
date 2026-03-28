import { FormEvent, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { api } from "../lib/api";
import type { Room } from "../types";
import { LoadingSpinner } from "../components/LoadingSpinner";
import { RoomCard } from "../components/RoomCard";

type SearchResponse = { rooms: Room[] };

export default function RoomsPage() {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    type: "",
    minPrice: "",
    maxPrice: "",
    checkIn: "",
    checkOut: ""
  });

  const loadRooms = async () => {
    setLoading(true);
    try {
      const hasDates = Boolean(filters.checkIn && filters.checkOut);
      if (hasDates) {
        const params = new URLSearchParams();
        params.set("checkIn", filters.checkIn);
        params.set("checkOut", filters.checkOut);
        if (filters.type) params.set("type", filters.type);
        if (filters.minPrice) params.set("minPrice", filters.minPrice);
        if (filters.maxPrice) params.set("maxPrice", filters.maxPrice);
        const data = await api<SearchResponse>(
          `/api/public/bookings/search-availability?${params.toString()}`
        );
        setRooms(data.rooms);
      } else {
        const params = new URLSearchParams();
        if (filters.type) params.set("type", filters.type);
        if (filters.minPrice) params.set("minPrice", filters.minPrice);
        if (filters.maxPrice) params.set("maxPrice", filters.maxPrice);
        const data = await api<Room[]>(`/api/public/rooms?${params.toString()}`);
        setRooms(data);
      }
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRooms();
  }, []);

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    loadRooms();
  };

  return (
    <div className="container page-stack">
      <div className="section-header">
        <div>
          <h1>Room listing</h1>
          <p>Search by budget, category, and availability dates.</p>
        </div>
      </div>

      <form className="filter-grid card" onSubmit={onSubmit}>
        <select value={filters.type} onChange={(e) => setFilters((current) => ({ ...current, type: e.target.value }))}>
          <option value="">All room types</option>
          <option value="STANDARD">Standard</option>
          <option value="DELUXE">Deluxe</option>
          <option value="SUITE">Suite</option>
          <option value="FAMILY">Family</option>
          <option value="EXECUTIVE">Executive</option>
        </select>
        <input
          type="number"
          placeholder="Min price"
          value={filters.minPrice}
          onChange={(e) => setFilters((current) => ({ ...current, minPrice: e.target.value }))}
        />
        <input
          type="number"
          placeholder="Max price"
          value={filters.maxPrice}
          onChange={(e) => setFilters((current) => ({ ...current, maxPrice: e.target.value }))}
        />
        <input
          type="date"
          value={filters.checkIn}
          onChange={(e) => setFilters((current) => ({ ...current, checkIn: e.target.value }))}
        />
        <input
          type="date"
          value={filters.checkOut}
          onChange={(e) => setFilters((current) => ({ ...current, checkOut: e.target.value }))}
        />
        <button className="primary-button" type="submit">
          Search
        </button>
      </form>

      {loading ? (
        <LoadingSpinner label="Searching rooms..." />
      ) : rooms.length ? (
        <div className="room-grid">
          {rooms.map((room) => (
            <RoomCard key={room.id} room={room} />
          ))}
        </div>
      ) : (
        <div className="empty-state">No rooms matched your filters.</div>
      )}
    </div>
  );
}
