import React from 'react';
import { Book, BookUpdateRequest } from '../types';

interface Props {
    book: Book;
    onUpdate: (id: string, changes: BookUpdateRequest) => void;
    onDelete: (id: string) => void;
}

const STATUS_LABELS: Record<string, string> = {
    WANT_TO_READ: 'Want to Read',
    CURRENTLY_READING: 'Currently Reading',
    FINISHED: 'Finished',
};

export function BookCard({ book, onUpdate, onDelete }: Props) {
    return (
        <div className="book-card">
            <div className="book-cover">
                {book.coverUrl ? (
                    <img src={book.coverUrl} alt={book.title} />
                ) : (
                    <div className="no-cover">No Cover</div>
                )}
            </div>
            <div className="book-info">
                <h3 className="book-title">{book.title}</h3>
                {book.author && <p className="book-author">{book.author}</p>}
                {book.publicationYear && (
                    <p className="book-meta">{book.publicationYear}</p>
                )}
                {book.pageCount && (
                    <p className="book-meta">{book.pageCount} pages</p>
                )}

                <select
                    className="status-select"
                    value={book.status}
                    onChange={e =>
                        onUpdate(book.id, {
                            status: e.target.value as Book['status'],
                        })
                    }
                >
                    {Object.entries(STATUS_LABELS).map(([value, label]) => (
                        <option key={value} value={value}>
                            {label}
                        </option>
                    ))}
                </select>

                <div className="rating">
                    {[1, 2, 3, 4, 5].map(star => (
                        <button
                            key={star}
                            className={`star ${book.rating && book.rating >= star ? 'filled' : ''}`}
                            onClick={() => onUpdate(book.id, { rating: star })}
                        >
                            ★
                        </button>
                    ))}
                </div>
            </div>

            <button
                className="delete-button"
                onClick={() => onDelete(book.id)}
            >
                Remove
            </button>
        </div>
    );
}