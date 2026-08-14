export interface Book {
    id: string;
    sessionId: string;
    openLibraryId: string | null;
    title: string;
    author: string | null;
    coverUrl: string | null;
    publicationYear: number | null;
    edition: string | null;
    pageCount: number | null;
    status: 'WANT_TO_READ' | 'CURRENTLY_READING' | 'FINISHED';
    rating: number | null;
    createdAt: string;
    updatedAt: string;
}

export interface BookRequest {
    openLibraryId?: string;
    title: string;
    author?: string;
    coverUrl?: string;
    publicationYear?: number;
    edition?: string;
    pageCount?: number;
    status: 'WANT_TO_READ' | 'CURRENTLY_READING' | 'FINISHED';
    rating?: number;
}

export interface BookUpdateRequest {
    status?: 'WANT_TO_READ' | 'CURRENTLY_READING' | 'FINISHED';
    rating?: number;
}

export interface SearchResult {
    title: string;
    author_name?: string[];
    cover_i?: number;
    first_publish_year?: number;
    edition_count?: number;
    number_of_pages_median?: number;
    key?: string;
    isbn?: string[];
}

export interface SessionResponse {
    token: string;
    sessionId: string;
}