import { useCallback, useEffect, useState } from 'react';
import { addBook, deleteBook, getBooks, updateBook } from '../api/client';
import { Book, BookRequest, BookUpdateRequest } from '../types';

export function useBooks(sessionId: string | null) {
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchBooks = useCallback(async () => {
        if (!sessionId) return;
        setLoading(true);
        try {
            const data = await getBooks();
            setBooks(data);
        } catch (err) {
            setError('Failed to fetch books');
        } finally {
            setLoading(false);
        }
    }, [sessionId]);

    useEffect(() => {
        fetchBooks();
    }, [fetchBooks]);

    const add = async (book: BookRequest) => {
        const newBook = await addBook(book);
        setBooks(prev => [...prev, newBook]);
        return newBook;
    };

    const update = async (id: string, changes: BookUpdateRequest) => {
        await updateBook(id, changes);
        setBooks(prev =>
            prev.map(b => (b.id === id ? { ...b, ...changes } : b))
        );
    };

    const remove = async (id: string) => {
        await deleteBook(id);
        setBooks(prev => prev.filter(b => b.id !== id));
    };

    return { books, loading, error, add, update, remove, refetch: fetchBooks };
}