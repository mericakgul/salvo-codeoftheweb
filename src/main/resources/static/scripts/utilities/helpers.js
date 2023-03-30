const domainUrl = 'http://localhost:8080';
const messageBox = document.querySelector('#login-result');
const usernameInput = document.querySelector('#username');
const passwordInput = document.querySelector('#password');

export const fetchJson = url =>
    fetch(domainUrl + url).then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            alert('You need to log in to be able to see the game details!');
            window.location.href = '/web/games.html';
        } else {
            throw new Error('error: ' + response.statusText);
        }
    });

export function login(evt) {
    if (areFieldsValid()) {
        sendLoginRequest(evt);
    }
}

export function signup(evt) {
    if (areFieldsValid()) {
        sendSignupRequest(evt);
    }
}

export function logout() {
    fetch('/api/logout', {
        method: 'POST'
    }).then(response => {
        if (response.ok && response.status === 200) {
            alert('Successfully logged out!');
            window.location.href = '/web/games.html';
        }
    })
}

const fetchedGamesObject = await fetchJson('/api/games'); //Top level await. No need to be in async function.
export const loggedInPlayerUsername = fetchedGamesObject['player']['username'];


export function showPlayerUsername (username, loggedInPlayerUsernameArea) {
    const usernameText = document.createTextNode(username);
    loggedInPlayerUsernameArea.appendChild(usernameText);
}

function areFieldsValid() {
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

function sendLoginRequest(evt) {
    const form = evt.target.form;
    fetch('/api/login', {
        method: 'POST',
        headers: {
            "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: `username=${form.username.value}&password=${form.password.value}`
    }).then(response => {
        if (response.ok && response.status === 200) {
            alert('You are successfully logged in!')
            history.go(0);
        } else {
            messageBox.innerText = 'login failure';
        }
    });
}

function sendSignupRequest(evt) {
    fetch('/api/players', {
        method: 'POST',
        body: JSON.stringify({username: usernameInput.value, password: passwordInput.value}),
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    }).then((response) => {
        if (response.ok && response.status === 200) {
            alert('You are successfully signed up!');
            sendLoginRequest(evt);
        } else if (response.status === 403) {
            alert('This username is in use, choose another one.')
        }
    });
}

function isValidEmail(email) {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}
