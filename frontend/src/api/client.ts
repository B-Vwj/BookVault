const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const getToken = (): string | null => localStorage.getItem('bookvault_token');

const headers = (): HeadersInit => ({
    'Content-Type': 'application/json',
    ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}),
});

export async function createSession(): Promise<{ token: string; sessionId: string }> {
    const res = await fetch(`${API_BASE}/session`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to create session');
    return res.json();
}

export async function searchBooks(query: string) {
    const res = await fetch(`${API_BASE}/search?q=${encodeURIComponent(query)}`);
    if (!res.ok) throw new Error('Search failed');
    return res.json();
}

export async function searchByIsbn(isbn: string) {
    const res = await fetch(`${API_BASE}/search?isbn=${encodeURIComponent(isbn)}`);
    if (!res.ok) throw new Error('ISBN search failed');
    return res.json();
}

export async function getBooks() {
    const res = await fetch(`${API_BASE}/books`, { headers: headers() });
    if (!res.ok) throw new Error('Failed to fetch books');
    return res.json();
}

export async function addBook(book: any) {
    const res = await fetch(`${API_BASE}/books`, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify(book),
    });
    if (!res.ok) throw new Error('Failed to add book');
    return res.json();
}

export async function updateBook(id: string, update: any) {
    const res = await fetch(`${API_BASE}/books/${id}`, {
        method: 'PUT',
        headers: headers(),
        body: JSON.stringify(update),
    });
    if (!res.ok) throw new Error('Failed to update book');
}

export async function deleteBook(id: string) {
    const res = await fetch(`${API_BASE}/books/${id}`, {
        method: 'DELETE',
        headers: headers(),
    });
    if (!res.ok) throw new Error('Failed to delete book');
}