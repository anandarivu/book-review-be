-- Flyway migration: Initial schema and seed data

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    -- other fields as needed
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    author VARCHAR(64) NOT NULL,
    description VARCHAR(4096) NOT NULL,
    cover_image_url VARCHAR(256) NOT NULL,
    published_year INT NOT NULL
);

CREATE TABLE book_genres (
    book_id UUID NOT NULL,
    genre VARCHAR(64) NOT NULL,
    PRIMARY KEY (book_id, genre),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    review_text VARCHAR(1024) NOT NULL,
    rating INT NOT NULL,
    date DATE NOT NULL,
    book_id UUID NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Insert admin role and user
INSERT INTO roles (id, name) VALUES ('00000000-0000-0000-0000-000000000001', 'ADMIN');
INSERT INTO users (id, user_id, email, password, created_at, updated_at) VALUES (
    '00000000-0000-0000-0000-000000000002', 'admin', 'admin@example.com', '$2b$12$BD3eAZVgc8v8Ybg3Ii689OohBX0m.6hW134VQ2qN2O6c5BFmgVy5K', NOW(), NOW()
);
INSERT INTO users (id, user_id, email, password, created_at, updated_at) VALUES (
    '00000000-0000-0000-0000-000000000003', 'a3k', 'a3k@gmail.com', '$2b$12$dBsED4SQ/96qooQhAsxtLeQkNcCa5oVrvxDV5cK6cnKdfNb/uxHu6', NOW(), NOW()
);
INSERT INTO user_roles (user_id, role_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001');

-- Insert 5 sample books
INSERT INTO books (id, title, author, description, cover_image_url, published_year) VALUES
    ('00000000-0000-0000-0000-000000000101', 'Ulysses', 'James Joyce', 'Set in Dublin, the novel follows a day in the life of Leopold Bloom, an advertising salesman, as he navigates the city. The narrative, heavily influenced by Homers Odyssey, explores themes of identity, heroism, and the complexities of everyday life. It is renowned for its stream-of-consciousness style and complex structure, making it a challenging but rewarding read.', 'https://images.thegreatestbooks.org/8zstd324g345k18fj93i3lrmp22j', 2020),
    ('00000000-0000-0000-0000-000000000102', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Set in the summer of 1922, the novel follows the life of a young and mysterious millionaire, his extravagant lifestyle in Long Island, and his obsessive love for a beautiful former debutante. As the story unfolds, the millionaires dark secrets and the corrupt reality of the American dream during the Jazz Age are revealed. The narrative is a critique of the hedonistic excess and moral decay of the era, ultimately leading to tragic consequences.', 'https://images.thegreatestbooks.org/sjgwsul1as680aj6mt68izewif4x', 2021),
    ('00000000-0000-0000-0000-000000000103', 'In Search of Lost Time', 'Marcel Proust', 'This renowned novel is a sweeping exploration of memory, love, art, and the passage of time, told through the narrators recollections of his childhood and experiences into adulthood in the late 19th and early 20th century aristocratic France. The narrative is notable for its lengthy and intricate involuntary memory episodes, the most famous being the "madeleine episode". It explores the themes of time, space and memory, but also raises questions about the nature of art and literature, and the complex relationships between love, sexuality, and possession.', 'https://images.thegreatestbooks.org/bxhcb1vedy2zjjytojrqsqxaxroc', 2022),
    ('00000000-0000-0000-0000-000000000104', 'The Catcher in the Rye', 'J. D. Salinger', 'The novel follows the story of a teenager named Holden Caulfield, who has just been expelled from his prep school. The narrative unfolds over the course of three days, during which Holden experiences various forms of alienation and his mental state continues to unravel. He criticizes the adult world as "phony" and struggles with his own transition into adulthood. The book is a profound exploration of teenage rebellion, alienation, and the loss of innocence.', 'https://images.thegreatestbooks.org/2lg73ocsv9m277nyi4bl4ggr5au9', 2020),
    ('00000000-0000-0000-0000-000000000105', 'One Hundred Years of Solitude', ' Gabriel García Márquez', 'This novel is a multi-generational saga that focuses on the Buendía family, who founded the fictional town of Macondo. It explores themes of love, loss, family, and the cyclical nature of history. The story is filled with magical realism, blending the supernatural with the ordinary, as it chronicles the family`s experiences, including civil war, marriages, births, and deaths. The book is renowned for its narrative style and its exploration of solitude, fate, and the inevitability of repetition in history.', 'https://images.thegreatestbooks.org/zczsb1tdkvo582f59slp0oam4vg5', 2021),
    ('00000000-0000-0000-0000-000000000106', 'Nineteen Eighty Four', 'George Orwell', 'A dystopian novel set in a totalitarian society ruled by Big Brother. The story follows Winston Smith, a low-ranking member of the Party, who works at the Ministry of Truth and is responsible for altering historical records to fit the Party`s propaganda. As Winston becomes disillusioned with the oppressive regime, he begins to seek truth and rebellion, ultimately leading to his capture and re-education. The novel explores themes of surveillance, censorship, and the manipulation of truth.', 'https://images.thegreatestbooks.org/g6ad61qycr85wv0xd9zvwedc0v7i', 2022),
    ('00000000-0000-0000-0000-000000000107', 'Moby-Dick', 'Herman Melville', 'The novel is a detailed narrative of a vengeful sea captain`s obsessive quest to hunt down a giant white sperm whale that bit off his leg. The captain`s relentless pursuit, despite the warnings and concerns of his crew, leads them on a dangerous journey across the seas. The story is a complex exploration of good and evil, obsession, and the nature of reality, filled with rich descriptions of whaling and the sea.', 'https://images.thegreatestbooks.org/qcjc5ow73ifto3m3h4zx2yef9ty4', 2020),
    ('00000000-0000-0000-0000-000000000108', 'The Sound and the Fury', 'William Faulkner', 'The novel is a complex exploration of the tragic Compson family from the American South. Told from four distinct perspectives, the story unfolds through stream of consciousness narratives, each revealing their own understanding of the family`s decline. The characters grapple with post-Civil War societal changes, personal loss, and their own mental instability. The narrative is marked by themes of time, innocence, and the burdens of the past.', 'https://images.thegreatestbooks.org/kvwsz99nxjtmrqw47ceffs2tbu8v', 2021),
    ('00000000-0000-0000-0000-000000000109', 'Don Quixote', 'Miguel de Cervantes', 'This classic novel follows the adventures of a man who, driven mad by reading too many chivalric romances, decides to become a knight-errant and roam the world righting wrongs under the name Don Quixote. Accompanied by his loyal squire, Sancho Panza, he battles windmills he believes to be giants and champions the virtuous lady Dulcinea, who is in reality a simple peasant girl. The book is a richly layered critique of the popular literature of Cervantes` time and a profound exploration of reality and illusion, madness and sanity.', 'https://images.thegreatestbooks.org/bjr8abyhldf8r37y0m8xno5sjqly', 2022),
    ('00000000-0000-0000-0000-000000000110', 'Anna Karenina', ' Leo Tolstoy', 'Set in 19th-century Russia, this novel revolves around the life of Anna Karenina, a high-society woman who, dissatisfied with her loveless marriage, embarks on a passionate affair with a charming officer named Count Vronsky. This scandalous affair leads to her social downfall, while parallel to this, the novel also explores the rural life and struggles of Levin, a landowner who seeks the meaning of life and true happiness. The book explores themes such as love, marriage, fidelity, societal norms, and the human quest for happiness.', 'https://images.thegreatestbooks.org/s9lv36qx0r3kwrahplenzroc7buh', 2020),
    ('00000000-0000-0000-0000-000000000111', 'Crime and Punishment', 'Fyodor Dostoevsky', 'A young, impoverished former student in Saint Petersburg, Russia, formulates a plan to kill an unscrupulous pawnbroker to redistribute her wealth among the needy. However, after carrying out the act, he is consumed by guilt and paranoia, leading to a psychological battle within himself. As he grapples with his actions, he also navigates complex relationships with a variety of characters, including a virtuous prostitute, his sister, and a relentless detective. The narrative explores themes of morality, redemption, and the psychological impacts of crime.', 'https://images.thegreatestbooks.org/7c2iucbwfk8op4at81czhlfklajc', 2021),
    ('00000000-0000-0000-0000-000000000112', 'Pride and Prejudice', 'Jane Austen', 'Set in early 19th-century England, this classic novel revolves around the lives of the Bennet family, particularly the five unmarried daughters. The narrative explores themes of manners, upbringing, morality, education, and marriage within the society of the landed gentry. It follows the romantic entanglements of Elizabeth Bennet, the second eldest daughter, who is intelligent, lively, and quick-witted, and her tumultuous relationship with the proud, wealthy, and seemingly aloof Mr. Darcy. Their story unfolds as they navigate societal expectations, personal misunderstandings, and their own pride and prejudice.', 'https://images.thegreatestbooks.org/6o2iawyerjzuwiwhe0j39o1sw90m', 2022),
    ('00000000-0000-0000-0000-000000000113', 'Lolita', 'Vladimir Nabokov', 'The novel tells the story of Humbert Humbert, a man with a disturbing obsession for young girls, or "nymphets" as he calls them. His obsession leads him to engage in a manipulative and destructive relationship with his 12-year-old stepdaughter, Lolita. The narrative is a controversial exploration of manipulation, obsession, and unreliable narration, as Humbert attempts to justify his actions and feelings throughout the story.', 'https://images.thegreatestbooks.org/0th7l09achni1gfehl4fgx4dvl1z', 2020),
    ('00000000-0000-0000-0000-000000000114', 'War and Peace', ' Leo Tolstoy', 'Set in the backdrop of the Napoleonic era, the novel presents a panorama of Russian society and its descent into the chaos of war. It follows the interconnected lives of five aristocratic families, their struggles, romances, and personal journeys through the tumultuous period of history. The narrative explores themes of love, war, and the meaning of life, as it weaves together historical events with the personal stories of its characters.', 'https://images.thegreatestbooks.org/24wtdlcfgr3neehj6ev3k4de5ley', 2021),
    ('00000000-0000-0000-0000-000000000115', 'Wuthering Heights', ' by Emily Bronte', 'This classic novel is a tale of love, revenge and social class set in the Yorkshire moors. It revolves around the intense, complex relationship between Catherine Earnshaw and Heathcliff, an orphan adopted by Catherine`s father. Despite their deep affection for each other, Catherine marries Edgar Linton, a wealthy neighbor, leading Heathcliff to seek revenge on the two families. The story unfolds over two generations, reflecting the consequences of their choices and the destructive power of obsessive love.', 'https://images.thegreatestbooks.org/g9iqq5htqekcnf605wk2h5agrqr1', 2022),
    ('00000000-0000-0000-0000-000000000116', 'The Lord of the Rings', 'J. R. R. Tolkien', 'This epic high-fantasy novel centers around a modest hobbit who is entrusted with the task of destroying a powerful ring that could enable the dark lord to conquer the world. Accompanied by a diverse group of companions, the hobbit embarks on a perilous journey across Middle-earth, battling evil forces and facing numerous challenges. The narrative, rich in mythology and complex themes of good versus evil, friendship, and heroism, has had a profound influence on the fantasy genre.', 'https://images.thegreatestbooks.org/a10wtqexxg0ue86e5natr3m2bk4r', 2020),
    ('00000000-0000-0000-0000-000000000117', 'To Kill a Mockingbird', 'Harper Lee', 'Set in the racially charged South during the Depression, the novel follows a young girl and her older brother as they navigate their small town`s societal norms and prejudices. Their father, a lawyer, is appointed to defend a black man falsely accused of raping a white woman, forcing the children to confront the harsh realities of racism and injustice. The story explores themes of morality, innocence, and the loss of innocence through the eyes of the young protagonists.', 'https://images.thegreatestbooks.org/nz51w5ff6g8hyo913f70lvm1u9tm', 2021),
    ('00000000-0000-0000-0000-000000000118', 'The Brothers Karamazov', 'Fyodor Dostoevsky', 'This classic novel explores the complex, passionate, and troubled relationship between four brothers and their father in 19th century Russia. The narrative delves into the themes of faith, doubt, morality, and redemption, as each brother grapples with personal dilemmas and family conflicts. The story culminates in a dramatic trial following a murder, which serves as a microcosm of the moral and philosophical struggles faced by each character, and by extension, humanity itself.', 'https://images.thegreatestbooks.org/1i7e37my0w129jnnbyeowbzdmee7', 2022),
    ('00000000-0000-0000-0000-000000000119', 'The Trial', 'Franz Kafka', 'The book revolves around a bank clerk who wakes one morning to find himself under arrest for an unspecified crime. Despite not being detained, he is subjected to the psychological torment of a bizarre and nightmarish judicial process. The story is a critique of bureaucracy, exploring themes of guilt, alienation and the inefficiency of the justice system.', 'https://images.thegreatestbooks.org/7b0fd36ecjwago9wfo7lwqx2b2vx', 2020),
    ('00000000-0000-0000-0000-000000000120', 'The Stranger', 'Albert Camus', 'Set in the sun-drenched landscapes of Algeria, this existential novel follows the life of an emotionally detached and indifferent man who becomes embroiled in a series of events leading to a senseless murder. Through his trial and eventual conviction, the narrative explores themes of absurdity, the meaning of life, and the societal expectations of morality. The protagonist`s passive acceptance of his fate and his refusal to conform to conventional emotional responses challenge the reader to question the nature of existence and the human condition.', 'https://images.thegreatestbooks.org/0sa0v6smsbfv7d01krq0zx9v25nu', 2021),
    ('00000000-0000-0000-0000-000000000121', 'Madame Bovary', 'Gustave Flaubert', 'Madame Bovary is a tragic novel about a young woman, Emma Bovary, who is married to a dull, but kind-hearted doctor. Dissatisfied with her life, she embarks on a series of extramarital affairs and indulges in a luxurious lifestyle in an attempt to escape the banalities and emptiness of provincial life. Her desire for passion and excitement leads her down a path of financial ruin and despair, ultimately resulting in a tragic end.', 'https://images.thegreatestbooks.org/g9cpq9eng11ahx91z1081i8a2uta', 2022),
    ('00000000-0000-0000-0000-000000000122', 'Adventures of Huckleberry Finn', 'Mark Twain', 'The novel follows the journey of a young boy named Huckleberry Finn and a runaway slave named Jim as they travel down the Mississippi River on a raft. Set in the American South before the Civil War, the story explores themes of friendship, freedom, and the hypocrisy of society. Through various adventures and encounters with a host of colorful characters, Huck grapples with his personal values, often clashing with the societal norms of the time.', 'https://images.thegreatestbooks.org/2sd0f0udrpdsfbqzrklmf0hcg2c3', 2020),
    ('00000000-0000-0000-0000-000000000123', 'The Odyssey', 'Homer', 'This epic poem follows the Greek hero Odysseus on his journey home after the fall of Troy. It takes Odysseus ten years to reach Ithaca after the ten-year Trojan War. Along the way, he encounters many obstacles including mythical creatures, divine beings, and natural disasters. Meanwhile, back in Ithaca, his wife Penelope and son Telemachus fend off suitors vying for Penelope`s hand in marriage, believing Odysseus to be dead. The story concludes with Odysseus`s return, his slaughter of the suitors, and his reunion with his family.', 'https://images.thegreatestbooks.org/lptzahvxxp15i1syy75b1bkd9pbc', 2021),
    ('00000000-0000-0000-0000-000000000124', 'The Grapes of Wrath', 'John Steinbeck', 'The book follows the Joad family, Oklahoma farmers displaced from their land during the Great Depression. The family, alongside thousands of other "Okies," travel to California in search of work and a better life. Throughout their journey, they face numerous hardships and injustices, yet maintain their humanity through unity and shared sacrifice. The narrative explores themes of man`s inhumanity to man, the dignity of wrath, and the power of family and friendship, offering a stark and moving portrayal of the harsh realities of American migrant laborers during the 1930s.', 'https://images.thegreatestbooks.org/w02p4sc3t7ub8t4cxjrccai6ikba', 2022),
    ('00000000-0000-0000-0000-000000000125', 'Five Point Someone', 'Chetan Bhagat', 'Five Point Someone is a debut novel by Chetan Bhagat about three friends—Alok, Hari, and Ryan—struggling to survive at the prestigious Indian Institute of Technology (IIT). Facing immense parental and academic pressure, their low GPAs cause them to feel like failures, but the story focuses on their efforts to cope with the system`s challenges, maintain their friendship, and find their place beyond just their academic performance. The book explores themes of friendship, ambition, and the overwhelming demands of a competitive education system in India.', 'https://online.pubhtml5.com/hmeo/vfma/files/large/1.jpg?1613976958', 2020),
    ('00000000-0000-0000-0000-000000000126', 'Half Girlfriend', 'Chetan Bhagat', 'Half Girlfriend is the story of Madhav Jha, a rural Bihari boy with a limited English vocabulary, who falls in love with the wealthy, Delhi-based Riya Somani after meeting her at a prestigious college through a sports quota. Their friendship deepens, but Riya, a reserved and less expressive person, only agrees to a "half-girlfriend" relationship, which means friendship without physical intimacy. Madhav`s attempts to get closer are rejected, leading to their separation, but fate brings them together again when Madhav needs help with English to impress the Bill Gates Foundation for a grant to fund his rural school`s toilets. Riya coaches him, they succeed, and she eventually confesses her love in a letter, but it`s too late as she has passed away.', 'https://rukminim2.flixcart.com/image/704/844/xif0q/book/r/g/i/half-girlfriend-original-imahepgmdkd9kwr2.jpeg', 2021),
    ('00000000-0000-0000-0000-000000000127', '2 States', 'Chetan Bhagat', '2 States is a story about a Punjabi man and a Tamil woman who fall in love and must overcome significant cultural differences and parental disapproval to get married. The plot humorously explores the cultural clash between North Indian and South Indian customs, chronicling their journey to gain their families` consent for marriage. ', 'https://m.media-amazon.com/images/I/81ApqY6HZlL.jpg', 2022);

-- Insert genres for sample books
INSERT INTO book_genres (book_id, genre) VALUES
    ('00000000-0000-0000-0000-000000000101', 'Fiction'),
    ('00000000-0000-0000-0000-000000000102', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000103', 'Science'),
    ('00000000-0000-0000-0000-000000000104', 'Fiction'),
    ('00000000-0000-0000-0000-000000000105', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000106', 'Science'),
    ('00000000-0000-0000-0000-000000000107', 'Fiction'),
    ('00000000-0000-0000-0000-000000000108', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000109', 'Science'),
    ('00000000-0000-0000-0000-000000000110', 'Fiction'),
    ('00000000-0000-0000-0000-000000000111', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000112', 'Science'),
    ('00000000-0000-0000-0000-000000000113', 'Fiction'),
    ('00000000-0000-0000-0000-000000000114', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000115', 'Science'),
    ('00000000-0000-0000-0000-000000000116', 'Fiction'),
    ('00000000-0000-0000-0000-000000000117', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000118', 'Science'),
    ('00000000-0000-0000-0000-000000000119', 'Fiction'),
    ('00000000-0000-0000-0000-000000000120', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000121', 'Science'),
    ('00000000-0000-0000-0000-000000000122', 'Fiction'),
    ('00000000-0000-0000-0000-000000000123', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000124', 'Science'),
    ('00000000-0000-0000-0000-000000000125', 'Fiction'),
    ('00000000-0000-0000-0000-000000000126', 'Non-Fiction'),
    ('00000000-0000-0000-0000-000000000127', 'Science');

-- You can add more sample data as needed
