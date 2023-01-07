export const fetchJson = url =>
    fetch(url).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('error: ' + response.statusText);
        }
    });