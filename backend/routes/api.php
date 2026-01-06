<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\CostumeController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('force_json')->group(function () {
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login', [AuthController::class, 'login']);

    Route::get('/costumes', [CostumeController::class, 'index']);

    Route::middleware('auth:sanctum')->group(function () {
        Route::get('/user', function (Request $request) {
            return $request->user();
        });

        Route::post('/costumes', [CostumeController::class, 'store']); // Should be admin only in real app
        Route::get('/costumes/{id}', [CostumeController::class, 'show']);
        Route::put('/costumes/{id}', [CostumeController::class, 'update']);
        Route::delete('/costumes/{id}', [CostumeController::class, 'destroy']);
    });
});
