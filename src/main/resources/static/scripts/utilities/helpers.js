export const usernameInput = document.querySelector('#username');
export const passwordInput = document.querySelector('#password');

export function showPlayerUsername (username, loggedInPlayerUsernameArea) {
    const usernameText = document.createTextNode(username);
    loggedInPlayerUsernameArea.appendChild(usernameText);
}

export function areFieldsValid() {
    if (!usernameInput.value || !passwordInput.value) {
        alert('Please fill both username and password fields in.');
        return;
    }
    if (!isValidEmail(usernameInput.value)) {
        alert('Please fill a valid email address in.');
        return;
    }
    return true;
}
export function nextChar(c) {
    return String.fromCharCode(c.charCodeAt(0) + 1);
}
export function combineShipsLocations(ownerShips) {
    return ownerShips.reduce((combinedLocationsArray, {shipLocations}) => {
        combinedLocationsArray.push(...shipLocations);
        return combinedLocationsArray;
    }, []);
}

function isValidEmail(email) {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}
