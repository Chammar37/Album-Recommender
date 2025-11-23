import '@testing-library/jest-dom';

// Mock fetch globally
global.fetch = vi.fn();

// Helper to create mock response
export function createMockResponse(data, ok = true, status = 200) {
    return {
        ok,
        status,
        json: () => Promise.resolve(data),
        text: () => Promise.resolve(JSON.stringify(data)),
    };
}

// Reset mocks before each test
beforeEach(() => {
    vi.clearAllMocks();
});
