import { useEffect, useState } from 'react';
import { createSession } from '../api/client';

const TOKEN_KEY = 'bookvault_token';
const SESSION_KEY = 'bookvault_session_id';

export function useSession() {
    const [sessionId, setSessionId] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const init = async () => {
            try {
                const existingToken = localStorage.getItem(TOKEN_KEY);
                const existingSessionId = localStorage.getItem(SESSION_KEY);

                if (existingToken && existingSessionId) {
                    setSessionId(existingSessionId);
                    setLoading(false);
                    return;
                }

                const session = await createSession();
                localStorage.setItem(TOKEN_KEY, session.token);
                localStorage.setItem(SESSION_KEY, session.sessionId);
                setSessionId(session.sessionId);
            } catch (err) {
                setError('Failed to initialize session');
            } finally {
                setLoading(false);
            }
        };

        init();
    }, []);

    return { sessionId, loading, error };
}