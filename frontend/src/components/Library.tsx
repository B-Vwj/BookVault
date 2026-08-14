import React from 'react';
import { Book, BookUpdateRequest } from '../types';
import { BookCard } from './BookCard';

interface Props {
    books: Book[];
    loading: boolean;
    onUpdate: (id: string, changes: BookUpdateRequest) => void;
    onDelete: (id: string) => void;
}

export function Library({ books, loading, onUpdate, onDelete }: Props) {
    if (loading) return <p className="status-text">Loading your library...</p>;

    if (books.length === 0) {
        return (
            <div className="empty-library">
                <p>Your library is empty. Search for books to get started.</p>
            </div>
        );
    }

    return (
        <div className="library">
            <h2 className="section-title">Your Library ({books.length})</h2>
            <div className="book-grid">
                {books.map(book => (
                    <BookCard
                        key={book.id}
                        book={book}
                        onUpdate={onUpdate}
                        onDelete={onDelete}
                    />
                ))}
            </div>
        </div>
    );
}