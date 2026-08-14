import React, { useState } from 'react';

interface Props {
    onSearch: (query: string) => void;
    loading: boolean;
}

export function SearchBar({ onSearch, loading }: Props) {
    const [query, setQuery] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (query.trim()) onSearch(query.trim());
    };

    return (
        <form onSubmit={handleSubmit} className="search-bar">
            <input
                type="text"
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Search by title or ISBN..."
                disabled={loading}
                className="search-input"
            />
            <button type="submit" disabled={loading || !query.trim()} className="search-button">
                {loading ? 'Searching...' : 'Search'}
            </button>
        </form>
    );
}