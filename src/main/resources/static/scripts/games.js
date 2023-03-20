import {fetchJson, login, signup, logout, showPlayerUsername, loggedInPlayerUsername} from "./utilities/helpers.js";

const leaderboard = document.querySelector('#leaderboard');
const gamesList = document.getElementById("games-list");
const loggedInPlayerUsernameArea = document.getElementById("logged-in-player");
const loginBtn = document.querySelector('#login-btn');
const logoutBtn = document.querySelector('#logout-btn');
const signupBtn = document.querySelector('#signup-btn');
const loginForm = document.querySelector('#login-form');
const fetchedGamesObject = await fetchJson('/api/games'); //Top level await. No need to be in async function.
const fetchedGamesList = fetchedGamesObject['games'];

loginBtn.addEventListener('click', evt => login(evt));

signupBtn.addEventListener('click', (evt) => signup(evt));

if (loggedInPlayerUsername) {
    showPlayerUsername(loggedInPlayerUsername, loggedInPlayerUsernameArea);
    loggedInPlayerUsernameArea.setAttribute('style', 'visibility: visible');
    loginForm.remove();
    logoutBtn.setAttribute('style', 'visibility: visible');
    logoutBtn.addEventListener('click', () => logout());
}

const briefGameInfo = (games) => {
    return games.map(game =>
        game['created'].toLocaleString() + ', ' +
        game['gamePlayers'].map(gamePlayer => gamePlayer['player']['username']).sort().join(', '));
}
briefGameInfo(fetchedGamesList).forEach(createHtmlListOfGames);

const scoresOfPlayers = (games) => {
    const playerList = createPlayerListFromJson(games);
    return playerList.reduce((scoresOfPlayers, player) => {
        const playerResult = {
            'name': player,
            'total': getTotalScoreOfPlayer(player, games),
            'won': getTotalWinCountOfPlayer(player, games),
            'lost': getTotalLossCountOfPlayer(player, games),
            'tied': getTotalTieCountOfPlayer(player, games)
        }
        scoresOfPlayers.push(playerResult);
        return scoresOfPlayers;
    }, []).sort((firstPlayer, secondPlayer) => secondPlayer['total'] - firstPlayer['total']);
}
scoresOfPlayers(fetchedGamesList).forEach(createLeaderboardTable);


function createHtmlListOfGames(gameInfo) {
    const listItem = document.createElement('li');
    const listItemText = document.createTextNode(gameInfo);
    listItem.appendChild(listItemText);
    gamesList.appendChild(listItem);
}

function createLeaderboardTable(player) {
    const tableRow = document.createElement('tr');
    Object.keys(player).forEach(key => {
        const tableCell = document.createElement('td');
        const tableCellText = document.createTextNode(player[key]);
        tableCell.appendChild(tableCellText);
        tableRow.appendChild(tableCell);
    });
    leaderboard.appendChild(tableRow);
}

function createPlayerListFromJson(games) {
    return games.reduce((playerList, {gamePlayers}) => {
        gamePlayers.map(({player}) => player['username'])
            .forEach(username => {
                if (!playerList.includes(username)) {
                    playerList.push(username);
                }
            });
        return playerList;
    }, []);
}

function getTotalScoreOfPlayer(playerUsername, games) {
    return games.reduce((totalScore, {gamePlayers}) => {
        gamePlayers.forEach(gamePlayer => {
            if (gamePlayer['player']['username'] === playerUsername) {
                totalScore += gamePlayer['score'];
            }
        })
        return totalScore;
    }, 0);
}

function getTotalWinCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'win');
}

function getTotalTieCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'tie');
}

function getTotalLossCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'loss');
}

function resultCounter(playerUsername, games, resultType) {
    const score = resultType === 'win' ? 1.0 : resultType === 'tie' ? 0.5 : 0.0;
    return games.reduce((counter, {gamePlayers}) => {
        gamePlayers.forEach(gamePlayer => {
            if (gamePlayer['player']['username'] === playerUsername && gamePlayer['score'] === score) {
                counter += 1;
            }
        });
        return counter;
    }, 0);
}
