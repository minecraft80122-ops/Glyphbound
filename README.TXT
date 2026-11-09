LEGENDA DEMAK - Greenfoot project  (progress build)
=====================================================

HOW TO RUN
  Open the folder in Greenfoot, then right-click TitleWorld -> new TitleWorld(), press Run.

WHAT THE GAME DOES NOW
  Title -> prolog story -> OVERWORLD (your Designer.png map, scrolling camera)
    - walk with WASD/arrows, Q swaps Raden <-> Dyah, E talks / enters gates
    - 4 villagers (tips + story), 3 domain gates (N = Tirta Amerta, E = Surya Kencana,
      W = Watu Lengkara) and the Southern Port gate (needs all 3 glyphs)
    - each domain: puzzle level (barricades, crates, targets, gates) -> CARD battle vs a boss
      -> story page -> back to the map with the glyph collected
    - mini-map + glyph counter HUD, vignette effect, dialogue box with typewriter text

FILE MAP (what to read first)
  MyWorld.java        overworld: camera, map drawing, collision, dialogue, HUD
  Player.java         overworld hero (your original class, rebuilt)
  MapActor.java       parent of everything that stands on the big map
  Interactable.java   interface: "things you can press E on" (Npc, DomainGate)
  Npc.java + ElderNpc/MerchantNpc/GuardNpc/FisherNpc   villagers (copy one to add a new one!)
  DomainGate.java     doorway to a temple level
  SpriteSheet / CharacterArt / HeroAnimator   loading + playing your sprite sheets
  GameState.java      story flow and shared data (stage numbers, glyphs collected)
  ExplorationWorld.java + the 3 level classes   temple puzzle levels
  BattleWorld.java    TURN-BASED CARD BATTLE (hand, energy, draw/discard, enemy intents, rewards)
  Card.java           card data - add new cards here (one small factory method each)
  CardActor.java      draws a card on screen (colour = owner: red/blue Raden, green/teal Dyah)
  EnemyType.java      enemy stats + attack patterns (A/H/D); BattleEnemy.java draws & runs them
  BattleHero.java     Raden / Dyah animation on the battle screen
  GlyphPuzzle.java + 3 puzzles    quick puzzles during battle

EDITING THE MAP COLLISION
  images/walkmask.png is 192x128 pixels. ONE pixel = an 8x8 square of Designer.png.
  White = walkable, black = blocked. Open it in any paint program, zoom in and paint.
  (walkmask_preview.png in the project folder shows it as a red overlay on the map.)

PLACEHOLDER ART (replace when you have final art)
  images/raden/*.png  and  images/npc/*.png are recolors of Test_Player sheets.
  Temple and battle backgrounds are drawn by code in Scenery.java.
  Enemies are drawn by code in Sprites.java.

CARD BATTLE RULES (based on the Glyphbound prototype)
  3 energy + 5 cards per turn. Click a card (attacks then need a click on an enemy), End Turn when done.
  Raden's attack cards MARK enemies; Dyah's attack cards on a marked enemy = KOMBO (damage x2).
  Every card charges the Glif meter; when full, click the Glif button -> timed puzzle -> glyph power.
  Bosses have a blue SHIELD: only KOMBO hits and glyph powers break it fast; a broken shield stuns the boss.
  After each win: full heal + choose 1 of 3 new cards (the deck keeps growing between fights).

IDEAS FOR NEXT STEPS (good TODOs)
  1. Final boss battle at the Southern Port (see TODO in GameState.STAGE_ENDING)
  2. Sound effects and music (put files in the sounds folder, use Greenfoot.playSound)
  3. Better enemy/boss sprites, real Raden art, more cards and a deck-viewer screen
  4. More NPCs and a side quest (new class that extends Npc)
  5. Save/load, a pause menu, a game-over music sting
