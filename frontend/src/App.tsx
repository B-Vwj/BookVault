import React, { useState } from 'react';
import { searchBooks, searchByIsbn } from './api/client';
import { Library } from './components/Library';
import { SearchBar } from './components/SearchBar';
import { SearchResults } from './components/SearchResults';
import { useBooks } from './hooks/useBooks';
import { useSession } from './hooks/useSession';
import { BookRequest, SearchResult } from './types';

function App() {
    const { sessionId, loading: sessionLoading, error: sessionError } = useSession();
    const { books, loading: booksLoading, add, update, remove } = useBooks(sessionId);

    const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [view, setView] = useState<'library' | 'search'>('library');

    const handleSearch = async (query: string) => {
        setSearchLoading(true);
        setView('search');
        try {
            // Check if query looks like an ISBN (all digits, 10 or 13 chars)
            const isIsbn = /^\d{10}(\d{3})?$/.test(query.replace(/-/g, ''));
            const results = isIsbn
                ? await searchByIsbn(query)
                : await searchBooks(query);
            setSearchResults(Array.isArray(results) ? results : [results]);
        } catch (err) {
            console.error('Search failed:', err);
        } finally {
            setSearchLoading(false);
        }
    };

    const handleAddBook = async (result: SearchResult) => {
        const COVER_BASE = 'https://covers.openlibrary.org/b/id';
        const book: BookRequest = {
            openLibraryId: result.key || undefined,
            title: result.title,
            author: result.author_name?.[0],
            coverUrl: result.cover_i
                ? `${COVER_BASE}/${result.cover_i}-M.jpg`
                : undefined,
            publicationYear: result.first_publish_year,
            pageCount: result.number_of_pages_median,
            status: 'WANT_TO_READ',
        };
        await add(book);
        setView('library');
        setSearchResults([]);
    };

    if (sessionLoading) {
        return (
            <div className="app-loading">
                <p>Initializing BookVault...</p>
            </div>
        );
    }

    if (sessionError) {
        return (
            <div className="app-error">
                <p>Failed to connect to BookVault. Is the backend running?</p>
            </div>
        );
    }

    return (
        <div className="app">
            <header className="app-header">
                <h1 className="app-title">BookVault</h1>
                <nav className="app-nav">
                    <button
                        className={`nav-button ${view === 'library' ? 'active' : ''}`}
                        onClick={() => setView('library')}
                    >
                        My Library
                    </button>
                    <button
                        className={`nav-button ${view === 'search' ? 'active' : ''}`}
                        onClick={() => setView('search')}
                    >
                        Search
                    </button>
                </nav>
            </header>

            <main className="app-main">
                <SearchBar onSearch={handleSearch} loading={searchLoading} />

                {view === 'search' && (
                    <SearchResults
                        results={searchResults}
                        onAdd={handleAddBook}
                        loading={searchLoading}
                    />
                )}

                {view === 'library' && (
                    <Library
                        books={books}
                        loading={booksLoading}
                        onUpdate={update}
                        onDelete={remove}
                    />
                )}
            </main>
        </div>
    );
}

export default App;