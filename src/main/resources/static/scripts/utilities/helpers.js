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
export function combineLocationLists(locationObjects, locationType) {
    return locationObjects.flatMap(object => object[locationType])
        .map(location => location.toLowerCase());

    // Second way by using reduce:
    // return locationObjects.reduce((combinedLocationsArray, { [locationType]: loc }) => {
    //     combinedLocationsArray.push(...loc);
    //     return combinedLocationsArray;
    // }, []);
}

function isValidEmail(email) {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}
