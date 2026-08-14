import React from 'react';
import { SearchResult } from '../types';

interface Props {
    results: SearchResult[];
    onAdd: (result: SearchResult) => void;
    loading: boolean;
}

const COVER_BASE = 'https://covers.openlibrary.org/b/id';

export function SearchResults({ results, onAdd, loading }: Props) {
    if (loading) return <p className="status-text">Searching...</p>;
    if (results.length === 0) return null;

    return (
        <div className="search-results">
            <h2 className="section-title">Search Results</h2>
            <div className="book-grid">
                {results.map((result, index) => (
                    <div key={index} className="book-card">
                        <div className="book-cover">
                            {result.cover_i ? (
                                <img
                                    src={`${COVER_BASE}/${result.cover_i}-M.jpg`}
                                    alt={result.title}
                                />
                            ) : (
                                <div className="no-cover">No Cover</div>
                            )}
                        </div>
                        <div className="book-info">
                            <h3 className="book-title">{result.title}</h3>
                            {result.author_name && (
                                <p className="book-author">{result.author_name[0]}</p>
                            )}
                            {result.first_publish_year && (
                                <p className="book-meta">{result.first_publish_year}</p>
                            )}
                            {result.number_of_pages_median && (
                                <p className="book-meta">{result.number_of_pages_median} pages</p>
                            )}
                        </div>
                        <button
                            className="add-button"
                            onClick={() => onAdd(result)}
                        >
                            Add to Library
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}