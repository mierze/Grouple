<?php
	include_once('../db_connect.inc.php');

	if(!isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (g_id)";
		echo json_encode ( $result );
		exit();
	} 
        $id = $_POST['g_id'];
	$stmt = $mysqli->prepare("SELECT COUNT(eg.e_id) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL");
	
        $stmt->bind_param('s', $id);
	$stmt->execute();
	$stmt->bind_result($numParticipants);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
        if ($row_cnt > 0)
        {
            $stmt->fetch();
            $response["numParticipants"] = $numParticipants;
            
        }
        $stmt->close();
        $professionalStmt = $mysqli->prepare("SELECT COUNT(DISTINCT(eg.e_id)) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL and e.category = 'Professional'");
        $professionalStmt->bind_param('s', $id);
	$professionalStmt->execute();
	$professionalStmt->bind_result($numProfessional);
	$professionalStmt->store_result();
	$row_cnt = $professionalStmt->num_rows;
        if ($row_cnt > 0)
        {
            $professionalStmt->fetch();
            $response["numProfessional"] = $numProfessional;
        }
        $professionalStmt->close();
        
        $socialStmt = $mysqli->prepare("SELECT COUNT(DISTINCT(eg.e_id)) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL and e.category = 'Social'");
               $socialStmt->bind_param('s', $id);
	$socialStmt->execute();
	$socialStmt->bind_result($numSocial);
	$socialStmt->store_result();
	$row_cnt = $socialStmt->num_rows;
        if ($row_cnt > 0)
        {
            $socialStmt->fetch();
            $response["numSocial"] = $numSocial;
        }
       $socialStmt->close();
       
        $entertainmentStmt = $mysqli->prepare("SELECT COUNT(DISTINCT(eg.e_id)) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL and e.category = 'Entertainment'");
                $entertainmentStmt->bind_param('s', $id);
	$entertainmentStmt->execute();
	$entertainmentStmt->bind_result($numEntertainment);
	$entertainmentStmt->store_result();
	$row_cnt = $entertainmentStmt->num_rows;
        if ($row_cnt > 0)
        {
            $entertainmentStmt->fetch();
            $response["numEntertainment"] = $numEntertainment;
        }
        $entertainmentStmt->close();
        
        $fitnessStmt = $mysqli->prepare("SELECT COUNT(DISTINCT(eg.e_id)) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL and e.category = 'Fitness'");
                $fitnessStmt->bind_param('s', $id);
	$fitnessStmt->execute();
	$fitnessStmt->bind_result($numFitness);
	$fitnessStmt->store_result();
	$row_cnt = $fitnessStmt->num_rows;
        if ($row_cnt > 0)
        {
            $fitnessStmt->fetch();
            $response["numFitness"] = $numFitness;
        }
        $fitnessStmt->close();
        $natureStmt = $mysqli->prepare("SELECT COUNT(DISTINCT(eg.e_id)) FROM `e_groups` eg JOIN `events` e ON eg.e_id = e.e_id WHERE eg.g_id = ? AND e.eventstate = 'Ended' AND eg.rec_date IS NOT NULL and e.category = 'Nature'");
                $natureStmt->bind_param('s', $id);
	$natureStmt->execute();
	$natureStmt->bind_result($numNature);
	$natureStmt->store_result();
	$row_cnt = $natureStmt->num_rows;
        if ($row_cnt > 0)
        {
            $natureStmt->fetch();
            $response["numNature"] = $numNature;
        }
        $natureStmt->close();
        
        $response["success"] = 1;


	echo(json_encode($response));
	$mysqli->close();
?>