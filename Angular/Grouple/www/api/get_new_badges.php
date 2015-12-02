<?php
	include_once('db_connect.inc.php');
	//this php should check if the user has any new rewards and return the new ones / update the badges table
	//pertinent checks:
	//	Overall time checks:
	//	-count in eventscats 1-5, + total of all participated in
	//		1. Fetch all counts from server
	//		2. If count for each is over a certain level of badge, check with badges table that that badge is in there
	//			if not, update the table with the new badge and timestamp
	//		3. If there was an update, add this badge to a return array of new badges
	//	-count for created 1-5 / overall
	//		1. Same as above counts
	//	Recent activity checks:
	//	-event in every day of past week (daily doer or something, active)
	//		1. Same idea
	//	-event of every kind within a week (jack of all trades or something similar, diversity ?)
	//	-	1. Instead of fetching above in one statement, use five and merge
	//	Miscellaneous:
	//	-create a recurring event that successfully happens at least twice
	//		1. Update events table with recurring column id and add another event with the same id, null otherwise
	//		2. Count these up and if same id has more than 1 count with you as creator than boom
	//	-count of items brought to events (helping hand...)
	//		1. Either count total items with your email on it, or events where you brought at least 1
	//	-groups in? public group joined? public event joined (lots of good names for these couple)?
	//		1. Not sure how to decide if group was public when you joined, maybe it doesn't matter
	//		2. Count groups / events you are in that have public flag
	//	-Successful reattempt at creating an event
	//		1. Have a flag for failed event in events table that is set when it fails
	//		2. Allow the event row to be updated and reproposed and set eventstate back to proposed and keep fail flag true
	//
	//with all that info within the php, will need to reference badges table to see if any badge has leveled / tiered up,
	//or is new in general, these will need to be updated in that table for this email and returned in the json return from this file
	
	//ensure all inputs have been sent
	if(!isset($_GET['id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required GET parameters (id)";
		echo json_encode ($result );
		exit();
	}
	$id = $_GET['id'];
	
	//$newBadges;//make this hold the return of all badges, make it array
	$fetches = array();
	
	//count of social events attended / created
	$socialStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Social'");
	$socialStmt->bind_param('s', $id);
	$socialStmt->execute();
	$socialStmt->bind_result($count);
	$socialStmt->store_result();
	$row_cnt = $socialStmt->num_rows;
	if($row_cnt > 0)
	{
		$socialStmt->fetch();
		$fetches["socialCount"] = $count;
	}
	else
	{
		$fetches["socialCount"] = 0;
	}
	$socialStmt->close();
	
	//count of professional events attended / created
	$professionalStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Professional'");
	$professionalStmt->bind_param('s', $id);
	$professionalStmt->execute();
	$professionalStmt->bind_result($count);
	$professionalStmt->store_result();
	$row_cnt = $professionalStmt->num_rows;
	if($row_cnt > 0)
	{
		$professionalStmt->fetch();
		$fetches["professionalCount"] = $count;
	}
	else
	{
		$fetches["professionalCount"] = 0;
	}
	$professionalStmt->close();

	//count of entertainment events attended / created
	$entertainmentStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Entertainment'");
	$entertainmentStmt->bind_param('s', $id);
	$entertainmentStmt->execute();
	$entertainmentStmt->bind_result($count);
	$entertainmentStmt->store_result();
	$row_cnt = $entertainmentStmt->num_rows;
	if($row_cnt > 0)
	{
		$entertainmentStmt->fetch();
		$fetches["entertainmentCount"] = $count;
	}
	else
	{
		$fetches["entertainmentCount"] = 0;
	}
	$entertainmentStmt->close();

	//count of fitness events attended / created
	$fitnessStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Fitness'");
	$fitnessStmt->bind_param('s', $id);
	$fitnessStmt->execute();
	$fitnessStmt->bind_result($count);
	$fitnessStmt->store_result();
	$row_cnt = $fitnessStmt->num_rows;
	if($row_cnt > 0)
	{
		$fitnessStmt->fetch();
		$fetches["fitnessCount"] = $count;
	}
	else
	{
		$fetches["fitnessCount"] = 0;
	}
	$fitnessStmt->close();
	
	//count of nature events attended / created
	$natureStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Nature'");	
	$natureStmt->bind_param('s', $id);
	$natureStmt->execute();
	$natureStmt->bind_result($count);
	$natureStmt->store_result();
	$row_cnt = $natureStmt->num_rows;
	if($row_cnt > 0)
	{
		$natureStmt->fetch();
		$fetches["natureCount"] = $count;
	}
	else
	{
		$fetches["natureCount"] = 0;
	}
	$natureStmt->close();
	
	//count of public events attended 
	$publicEventStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.public = 1");
	$publicEventStmt->bind_param('s', $id);
	$publicEventStmt->execute();
	$publicEventStmt->bind_result($count);
	$publicEventStmt->store_result();
	$row_cnt = $publicEventStmt->num_rows;
	if($row_cnt > 0)
	{
		$publicEventStmt->fetch();
		$fetches["publicEventCount"] = $count;
	}
	else
	{
		$fetches["publicEventCount"] = 0;
	}
	$publicEventStmt->close();
	
	//calculating total events attended
	$fetches["totalCount"] = $fetches["socialCount"] + $fetches["entertainmentCount"] + $fetches["professionalCount"] + $fetches["fitnessCount"] + $fetches["natureCount"];
	
	//fetching count of professional events created
	$professionalCreateStmt = $mysqli->prepare("SELECT count(e_id) from `events` WHERE creator = ? AND eventstate = 'Ended' and events.category = 'Professional'");
	$professionalCreateStmt->bind_param('s', $id);
	$professionalCreateStmt->execute();
	$professionalCreateStmt->bind_result($count);
	$professionalCreateStmt->store_result();
	$row_cnt = $professionalCreateStmt->num_rows;
	if($row_cnt > 0)
	{
		$professionalCreateStmt->fetch();
		$fetches["professionalCreateCount"] = $count;
	}
	else
	{
		$fetches["professionalCreateCount"] = 0;
	}
	$professionalCreateStmt->close();
	
	//fetching count of nature events created
	$natureCreateStmt = $mysqli->prepare("SELECT count(e_id) from `events` WHERE creator = ? AND eventstate = 'Ended' and events.category = 'Nature'");
	$natureCreateStmt->bind_param('s', $id);
	$natureCreateStmt->execute();
	$natureCreateStmt->bind_result($count);
	$natureCreateStmt->store_result();
	$row_cnt = $natureCreateStmt->num_rows;
	if($row_cnt > 0)
	{
		$natureCreateStmt->fetch();
		$fetches["natureCreateCount"] = $count;
	}
	else
	{
		$fetches["natureCreateCount"] = 0;
	}
	$natureCreateStmt->close();
	
	//fetching count of entertainment events created
	$entertainmentCreateStmt = $mysqli->prepare("SELECT count(e_id) from `events` WHERE creator = ? AND eventstate = 'Ended' and events.category = 'Entertainment'");
	$entertainmentCreateStmt->bind_param('s', $id);
	$entertainmentCreateStmt->execute();
	$entertainmentCreateStmt->bind_result($count);
	$entertainmentCreateStmt->store_result();
	$row_cnt = $entertainmentCreateStmt->num_rows;
	if($row_cnt > 0)
	{
		$entertainmentCreateStmt->fetch();
		$fetches["entertainmentCreateCount"] = $count;
	}
	else
	{
		$fetches["entertainmentCreateCount"] = 0;
	}
	$entertainmentCreateStmt->close();

	//fetching count of professional events created
	$socialCreateStmt = $mysqli->prepare("SELECT count(e_id) from `events` WHERE creator = ? AND eventstate = 'Ended' and events.category = 'Social'");
	$socialCreateStmt->bind_param('s', $id);
	$socialCreateStmt->execute();
	$socialCreateStmt->bind_result($count);
	$socialCreateStmt->store_result();
	$row_cnt = $socialCreateStmt->num_rows;
	if($row_cnt > 0)
	{
		$socialCreateStmt->fetch();
		$fetches["socialCreateCount"] = $count;
	}
	$socialCreateStmt->close();
	
	//fetching count of fitness events created
	$fitnessCreateStmt = $mysqli->prepare("SELECT count(e_id) from `events` WHERE creator = ? AND eventstate = 'Ended' and events.category = 'Fitness'");
	$fitnessCreateStmt->bind_param('s', $id);
	$fitnessCreateStmt->execute();
	$fitnessCreateStmt->bind_result($count);
	$fitnessCreateStmt->store_result();
	$row_cnt = $fitnessCreateStmt->num_rows;
	if($row_cnt > 0)
	{
		$fitnessCreateStmt->fetch();
		$fetches["fitnessCreateCount"] = $count;
	}
	$fitnessCreateStmt->close();
	
	//adding up all events created count
	$fetches["totalCreateCount"] =  $fetches["socialCreateCount"] + $fetches["entertainmentCreateCount"] + $fetches["professionalCreateCount"] + $fetches["fitnessCreateCount"] + $fetches["natureCreateCount"];
	
	//fetching weekly badge, TO COME, WILL BE IMPLEMENTED LATER
	//for the current week
	//$socialWeeklyStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Social' and events.end_date > ...last week");		
	//check in each, if all greater than 1 -> jack of all trades	
	//recurring event success
	//$recurringStmt = $mysqli->prepare("SELECT count(events.e_id) from `events` join e_members on events.e_id = e_members.e_id WHERE e_members.email = ? AND events.eventstate = 'Ended' and events.category = 'Social' and events.end_date > ...last week");
		//count of nature events attended / created
	$helpingStmt = $mysqli->prepare("SELECT count(id) from items_tobring itb join events e on e.e_id = itb.e_id WHERE itb.email = ? AND e.eventstate = 'Ended'");
	$helpingStmt->bind_param('s', $id);
	$helpingStmt->execute();
	$helpingStmt->bind_result($count);
	$helpingStmt->store_result();
	$row_cnt = $helpingStmt->num_rows;
	if($row_cnt > 0)
	{
		$helpingStmt->fetch();
		$fetches["numItems"] = $count;
	}
	else
	{
		$fetches["numItems"] = 0;
	}
	$helpingStmt->close();
	
	//FIRST BADGES FIRST
	$response = array();
	$response["badges"] = array();
	//check badges table for each regular category badge
		//for each: levels = (1->1), (2->10), (3->25), (4->50), (5->100)
			//if new fetch is good enough for a new level, update
	//stmt to grab badge with the name for nature and greater than level qualified
	if ($fetches["socialCount"] > 0)
	{
		$name = "Gregarious";
		$level = getLevel($fetches["socialCount"], 10);
		$socialCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$socialCheckStmt->bind_param('sss', $id, $name, $level);
		$socialCheckStmt->execute();
		$socialCheckStmt->bind_result($old_level);
		$socialCheckStmt->store_result();
		$row_cnt = $socialCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$socialCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$socialUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ? where `email` = ? and name = ?");
				$socialUpdateStmt->bind_param('sss', $id, $level, $name);
				if ($socialUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$socialUpdateStmt->close();
			}
		}
		else
		{
			$socialUpdateStmt = $mysqli->prepare("INSERT into `badges` (email, name, b_level, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$socialUpdateStmt->bind_param('sss', $id, $name, $level);
			if ($socialUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$socialUpdateStmt->close();
		}		
	}
	
	if ($fetches["fitnessCount"] > 0)
	{
		$name = "Agile";
		$level = getLevel($fetches["fitnessCount"], 10);
		$fitnessCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$fitnessCheckStmt->bind_param('sss', $id, $name, $level);
		$fitnessCheckStmt->execute();
		$fitnessCheckStmt->bind_result($old_level);
		$fitnessCheckStmt->store_result();
		$row_cnt = $fitnessCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$fitnessCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$fitnessUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$fitnessUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($fitnessUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$fitnessUpdateStmt->close();
			}
		}
		else
		{
			$fitnessUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$fitnessUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($fitnessUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$fitnessUpdateStmt->close();
		}	
	}
	
	if ($fetches["professionalCount"] > 0)
	{
		$name = "Diligent";
		$level = getLevel($fetches["professionalCount"], 10);
		$professionalCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$professionalCheckStmt->bind_param('sss', $id, $name, $level);
		$professionalCheckStmt->execute();
		$professionalCheckStmt->bind_result($old_level);
		$professionalCheckStmt->store_result();
		$row_cnt = $professionalCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$professionalCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$professionalUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$professionalUpdateStmt->bind_param('sss',$level, $id, $name);
				if ($professionalUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$professionalUpdateStmt->close();
			}
		}
		else
		{
			$professionalUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$professionalUpdateStmt->bind_param('sss',$level, $id, $name);
			if ($professionalUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$professionalUpdateStmt->close();	
		}
	}
	
	
	if ($fetches["entertainmentCount"] > 0)
	{
		$name = "Amuser";
		$level = getLevel($fetches["entertainmentCount"], 10);
		$entertainmentCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$entertainmentCheckStmt->bind_param('sss', $id, $name, $level);
		$entertainmentCheckStmt->execute();
		$entertainmentCheckStmt->bind_result($old_level);
		$entertainmentCheckStmt->store_result();
		$row_cnt = $entertainmentCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$entertainmentCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$entertainmentUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$entertainmentUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($entertainmentUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$entertainmentUpdateStmt->close();
			}
		}
		else
		{
			$entertainmentUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$entertainmentUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($entertainmentUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$entertainmentUpdateStmt->close();			
		}
	}
	
	if ($fetches["natureCount"] > 0)
	{
		$name = "Outdoorsman";
		$level = getLevel($fetches["natureCount"], 10);
		$natureCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$natureCheckStmt->bind_param('sss', $id, $name, $level);
		$natureCheckStmt->execute();
		$natureCheckStmt->bind_result($old_level);
		$natureCheckStmt->store_result();
		$row_cnt = $natureCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$natureCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$natureUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$natureUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($natureUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$natureUpdateStmt->close();
			}
		}
		else
		{
			$natureUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$natureUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($natureUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$natureUpdateStmt->close();
		}
	}
	
	//stmt to grab badge with the name for nature and greater than level qualified
	if ($fetches["publicEventCount"] > 0)
	{
		$name = "Mingler";
		$level = getLevel($fetches["publicEventCount"], 5);
		$publicEventCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$publicEventCheckStmt->bind_param('sss', $id, $name, $level);
		$publicEventCheckStmt->execute();
		$publicEventCheckStmt->bind_result($old_level);
		$publicEventCheckStmt->store_result();
		$row_cnt = $publicEventCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$publicEventCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$publicEventUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ? where `email` = ? and name = ?");
				$publicEventUpdateStmt->bind_param('sss', $id, $level, $name);
				if ($publicEventUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				
				$publicEventUpdateStmt->close();
			}
			
		}
		else
		{
			$publicEventUpdateStmt = $mysqli->prepare("INSERT into `badges` (email, name, b_level, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$publicEventUpdateStmt->bind_param('sss', $id, $name, $level);
			if ($publicEventUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$publicEventUpdateStmt->close();
		}
		$publicEventCheckStmt->close();
	}
		
	//check badges table for total events attended badge
		//for each: levels = (1->10), (2->50), (3->100), (4->250), (5->500)
			//if new fetch is good enough for a new level, update
	if ($fetches["totalCount"] > 0)
	{
		$name = "Extrovert";
		$level = getLevel($fetches["totalCount"], 10);
		$totalCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$totalCheckStmt->bind_param('sss', $id, $name, $level);
		$totalCheckStmt->execute();
		$totalCheckStmt->bind_result($old_level);
		$totalCheckStmt->store_result();
		$row_cnt = $totalCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$totalCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$totalUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$totalUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($totalUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$totalUpdateStmt->close();
			}
		}
		else
		{
			$totalUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$totalUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($totalUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$totalUpdateStmt->close();
		}
	}
	
	if ($fetches["socialCount"] > 0 && $fetches["entertainmentCount"] > 0 && $fetches["professionalCount"] > 0 && $fetches["fitnessCount"] > 0 && $fetches["natureCount"] > 0)
	{
		$name = "Well Rounded";
		if ($fetches["socialCount"] < 2 && $fetches["entertainmentCount"] < 2 && $fetches["professionalCount"] < 2 && $fetches["fitnessCount"] < 2 && $fetches["natureCount"] < 2)
		{
			$level = 1;
		}
		else if ($fetches["socialCount"] < 5 && $fetches["entertainmentCount"] < 5 && $fetches["professionalCount"] < 5 && $fetches["fitnessCount"] < 10 && $fetches["natureCount"] < 10)
		{
			$level = 2;
		}
		else if ($fetches["socialCount"] < 25 && $fetches["entertainmentCount"] < 25 && $fetches["professionalCount"] < 5 && $fetches["fitnessCount"] < 5 && $fetches["natureCount"] < 5)
		{
			$level = 3;
		}
		else if ($fetches["socialCount"] < 10 && $fetches["entertainmentCount"] < 10 && $fetches["professionalCount"] < 10 && $fetches["fitnessCount"] < 10 && $fetches["natureCount"] < 10)
		{
			$level = 4;
		}
		else if ($fetches["socialCount"] < 25 && $fetches["entertainmentCount"] < 25 && $fetches["professionalCount"] < 25 && $fetches["fitnessCount"] < 25 && $fetches["natureCount"] < 25)
		{
			$level = 5;
		}
		$roundedCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$roundedCheckStmt->bind_param('sss', $id, $name, $level);
		$roundedCheckStmt->execute();
		$roundedCheckStmt->bind_result($old_level);
		$roundedCheckStmt->store_result();
		$row_cnt = $roundedCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$roundedCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$roundedUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ? where `email` = ? and name = ?");
				$roundedUpdateStmt->bind_param('sss', $id, $level, $name);
				if ($roundedUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						//add this to the return of this php
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$roundedUpdateStmt->close();
			}
		}
		else
		{
			$roundedUpdateStmt = $mysqli->prepare("INSERT into `badges` (email, name, b_level, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$roundedUpdateStmt->bind_param('sss', $id, $name, $level);
			if ($roundedUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					//add this to the return of this php
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$roundedUpdateStmt->close();
		}		
	}
	
	//check for balanced badge (does every event pretty close to evenly)
			
	//check badges table for each create category badge
		//for each: levels = (1->1), (2->5), (3->10), (4->25), (5->50)
			//if new fetch is good enough for a new level, update
	if ($fetches["socialCreateCount"] > 0)
	{
		$name = "Congregator";
		$level = getLevel($fetches["socialCreateCount"], 5);
		$socialCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$socialCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$socialCreateCheckStmt->execute();
		$socialCreateCheckStmt->bind_result($old_level);
		$socialCreateCheckStmt->store_result();
		$row_cnt = $socialCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$socialCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$socialCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$socialCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($socialCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$socialCreateUpdateStmt->close();
			}
		}
		else
		{
			$socialCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$socialCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($socialCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$socialCreateUpdateStmt->close();
		}
	}

	if ($fetches["professionalCreateCount"] > 0)
	{
		$name = "Productive";
		$level = getLevel($fetches["professionalCreateCount"], 5);
		$professionalCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$professionalCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$professionalCreateCheckStmt->execute();
		$professionalCreateCheckStmt->bind_result($old_level);
		$professionalCreateCheckStmt->store_result();
		$row_cnt = $professionalCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$professionalCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$professionalCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$professionalCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($professionalCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$professionalCreateUpdateStmt->close();
			}
		}
		else
		{
			$professionalCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$professionalCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($professionalCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$professionalCreateUpdateStmt->close();
		}
	}
	
	if ($fetches["entertainmentCreateCount"] > 0)
	{
		$name = "Merrymaker";
		$level = getLevel($fetches["entertainmentCreateCount"], 5);
		$entertainmentCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$entertainmentCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$entertainmentCreateCheckStmt->execute();
		$entertainmentCreateCheckStmt->bind_result($old_level);
		$entertainmentCreateCheckStmt->store_result();
		$row_cnt = $entertainmentCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$entertainmentCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$entertainmentCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$entertainmentCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($entertainmentCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$entertainmentCreateUpdateStmt->close();
			}
		}
		else
		{
			$entertainmentCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$entertainmentCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($entertainmentCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$entertainmentCreateUpdateStmt->close();
		}
	}
	
	if ($fetches["fitnessCreateCount"] > 0)
	{
		$name = "Health Nut";
		$level = getLevel($fetches["fitnessCreateCount"], 5);
		$fitnessCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$fitnessCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$fitnessCreateCheckStmt->execute();
		$fitnessCreateCheckStmt->bind_result($old_level);
		$fitnessCreateCheckStmt->store_result();
		$row_cnt = $fitnessCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$fitnessCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$fitnessCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$fitnessCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($fitnessCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$fitnessCreateUpdateStmt->close();
			}
		}
		else
		{
			$fitnessCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$fitnessCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($fitnessCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$fitnessCreateUpdateStmt->close();
		}
	}

	if ($fetches["natureCreateCount"] > 0)
	{
		$name = "Environmentalist";
		$level = getLevel($fetches["natureCreateCount"], 5);
		$natureCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$natureCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$natureCreateCheckStmt->execute();
		$natureCreateCheckStmt->bind_result($old_level);
		$natureCreateCheckStmt->store_result();
		$row_cnt = $natureCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$natureCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$natureCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$natureCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($natureCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$natureCreateUpdateStmt->close();
			}
		}
		else
		{
			$natureCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$natureCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($natureCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$natureCreateUpdateStmt->close();
		}
	}	
	
	
			
	//check badges table for totatl created badge
		//for each: levels = (1->5), (2->10), (3->25), (4->50), (5->100)
			//if new fetch is good enough for a new level, update
			
	if ($fetches["totalCreateCount"] >= 5)
	{
		$name = "Creator";
		$level = getLevel($fetches["totalCreateCount"], 10);
		$totalCreateCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$totalCreateCheckStmt->bind_param('sss', $id, $name, $level);
		$totalCreateCheckStmt->execute();
		$totalCreateCheckStmt->bind_result($old_level);
		$totalCreateCheckStmt->store_result();
		$row_cnt = $totalCreateCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$totalCreateCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$totalCreateUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$totalCreateUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($totalCreateUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$totalCreateUpdateStmt->close();
			}
		}
		else
		{
			$totalCreateUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$totalCreateUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($totalCreateUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$totalCreateUpdateStmt->close();
		}
	}
	
	if ($fetches["numItems"] > 0)
	{
		$name = "Helping Hand";
		$level = getLevel($fetches["numItems"], 5);
		$helpingCheckStmt = $mysqli->prepare("SELECT b_level from `badges` WHERE email = ? AND name = ? and b_level >= ?");
		$helpingCheckStmt->bind_param('sss', $id, $name, $level);
		$helpingCheckStmt->execute();
		$helpingCheckStmt->bind_result($old_level);
		$helpingCheckStmt->store_result();
		$row_cnt = $helpingCheckStmt->num_rows;
		if($row_cnt > 0)
		{
			$helpingCheckStmt->fetch();
			if ($level > $old_level)
			{
				//update
				$helpingUpdateStmt = $mysqli->prepare("UPDATE `badges` set b_level = ?, rec_date = CURRENT_TIMESTAMP where `email` = ? and name = ?");
				$helpingUpdateStmt->bind_param('sss', $level, $id, $name);
				if ($helpingUpdateStmt->execute())
				{
					if ($mysqli->affected_rows > 0)
					{
						$result = array();
						$result["name"] = $name;
						$result["level"] = $level;
						array_push($response["badges"], $result);
					}
				}
				$helpingUpdateStmt->close();
			}
		}
		else
		{
			$helpingUpdateStmt = $mysqli->prepare("INSERT into `badges` (b_level, email, name, rec_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
			$helpingUpdateStmt->bind_param('sss', $level, $id, $name);
			if ($helpingUpdateStmt->execute())
			{
				if ($mysqli->affected_rows > 0)
				{
					$result = array();
					$result["name"] = $name;
					$result["level"] = $level;
					array_push($response["badges"], $result);
				}
			}
			$helpingUpdateStmt->close();
		}
	}
	

	
	$response["success"] = 1;
	$response["message"] = "Successfully updated badges!";
	echo(json_encode($response));
	
	$mysqli->close();
	
	function getLevel($points, $threshold)
	{
		$i = 1;
		$nextLevel = $i*$threshold;
		
		while ($points > $nextLevel)
		{
			$i++;
			$nextLevel = $i * $threshold;
		}
		return $i;
	}
?>
	