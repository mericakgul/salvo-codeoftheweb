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
    return games.reduce((briefGameInfoList, game, index) => {
        const date = new Date(game['created']);
        const formattedDate = date.toLocaleString();
        const briefGameInfo = {
            'no': index + 1,
            'created-time': formattedDate,
            'first-player': game['gamePlayers'][0]['player']['username'],
            'second-player': game['gamePlayers'][1]?.['player']?.['username'] || '', // If there is a game, there must be created time and first player who is the creator but second player might not exist.
        }
        briefGameInfoList.push(briefGameInfo);
        return briefGameInfoList;
    }, []);
}

briefGameInfo(fetchedGamesList).forEach(createGamesListTable);


const scoresOfPlayers = (games) => {
    const playerList = createPlayerListFromJson(games);
    return playerList.reduce((scoresOfPlayers, player, index) => {
        const playerResult = {
            'no': index + 1,
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

function createGamesListTable(briefGameInfo, index) {
    createTable(briefGameInfo,gamesList, index);
}

function createLeaderboardTable(player) {
    createTable(player,leaderboard);
}

function createTable(data, tableName, index){
    const tableRow = document.createElement('tr');

    Object.keys(data).forEach(key => {
        const tableCell = document.createElement('td');
        const tableCellText = document.createTextNode(data[key]);
        tableCell.appendChild(tableCellText);
        tableRow.appendChild(tableCell);
    });

    if(tableName === gamesList){
        const rowId = index + 1;
        tableRow.setAttribute('data-id', `${rowId}`);
        createJoinButton(tableRow, data['second-player']);
    }
    tableName.appendChild(tableRow);
}

function createJoinButton(tableRow, secondPlayer) {
    const buttonCell = document.createElement('td');
    const button = document.createElement('button');
    button.classList.add('btn');
    button.classList.add('btn-primary');
    button.textContent = 'Join';
    if(secondPlayer) {
        button.disabled = true;
    }
    buttonCell.appendChild(button);
    tableRow.appendChild(buttonCell);
}

function createPlayerListFromJson(games) {
    return games.reduce((playerList, {gamePlayers}) => {
        gamePlayers.forEach(({player}) => {
            if (!playerList.includes(player['username'])) {
                playerList.push(player['username']);
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
