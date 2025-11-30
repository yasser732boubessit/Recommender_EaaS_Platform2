-- Supprimer l'ancienne table si elle existe
DROP TABLE IF EXISTS evaluation_results;

-- Créer la table "algorithms"
CREATE TABLE algorithms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url_items TEXT,
    url_events TEXT,
    url_reco TEXT
);

-- Créer la table "users"
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
INSERT INTO algorithms (name, url_items, url_events, url_reco) VALUES
('MostPopular', 'http://example.com/items/mostpopular', 'http://example.com/events/mostpopular', 'http://example.com/reco/mostpopular'),
('RecentlyPopular', 'http://example.com/items/recentlypopular', 'http://example.com/events/recentlypopular', 'http://example.com/reco/recentlypopular'),
('RecentlyClicked', 'http://example.com/items/recentlyclicked', 'http://example.com/events/recentlyclicked', 'http://example.com/reco/recentlyclicked'),
('Random', 'http://example.com/items/random', 'http://example.com/events/random', 'http://example.com/reco/random'),
('Hybrid', 'http://example.com/items/hybrid', 'http://example.com/events/hybrid', 'http://example.com/reco/hybrid');
