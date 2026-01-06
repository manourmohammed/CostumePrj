<?php

namespace App\Http\Controllers;

use App\Models\Costume;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Validator;

class CostumeController extends Controller
{
    /**
     * Récupérer tous les costumes avec pagination
     */
    public function index(Request $request)
    {
        try {
            $costumes = Costume::select('id', 'name', 'description', 'image_url', 'price', 'quantite', 'date_debut', 'date_fin')
                ->get();

            // Optimiser les URLs d'images si nécessaire
            $costumes->transform(function($costume) {
                // Si image_url est un chemin relatif, le convertir en URL complète
                if ($costume->image_url && !str_starts_with($costume->image_url, 'http')) {
                    $costume->image_url = url('storage/' . $costume->image_url);
                }
                return $costume;
            });

            return response()->json($costumes, 200);

        } catch (\Exception $e) {
            Log::error('Erreur lors de la récupération des costumes: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur lors de la récupération des costumes',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Créer un nouveau costume
     */
    public function store(Request $request)
    {
        try {
            $validator = Validator::make($request->all(), [
                'name' => 'required|string|max:255',
                'description' => 'nullable|string|max:1000',
                'image_url' => 'nullable|image|max:2048',
                 // Validation pour le fichier image
                'price' => 'required|numeric|min:0',
                'date_debut' => 'nullable|date',
                'date_fin' => 'nullable|date|after_or_equal:date_debut',
                'quantite' => 'required|integer|min:0',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'error' => 'Validation échouée',
                    'messages' => $validator->errors()
                ], 422);
            }

            $data = $validator->validated();

            // Gérer le téléchargement de l'image
            if ($request->hasFile('image')) {
                $path = $request->file('image')->store('costumes', 'public');
                $data['image_url'] = $path;
            }

            // Gérer le format de date human-readable provenant d'Android
            if (isset($data['date_debut'])) {
                try {
                    $data['date_debut'] = \Carbon\Carbon::parse($data['date_debut'])->format('Y-m-d');
                } catch (\Exception $e) {
                    // Laisser tel quel si le parsing échoue
                }
            }

            if (isset($data['date_fin'])) {
                try {
                    $data['date_fin'] = \Carbon\Carbon::parse($data['date_fin'])->format('Y-m-d');
                } catch (\Exception $e) {
                    // Laisser tel quel
                }
            }

            // Supprimer le champ 'image' du tableau data s'il existe avant de créer en DB
            // (car image_url contient déjà le chemin)
            unset($data['image']);

            $costume = Costume::create($data);

            return response()->json([
                'message' => 'Costume créé avec succès',
                'costume' => $costume
            ], 201);

        } catch (\Exception $e) {
            Log::error('Erreur lors de la création du costume: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur lors de la création',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Afficher un costume spécifique
     */
    public function show($id)
    {
        try {
            $costume = Costume::findOrFail($id);

            // Optimiser l'URL de l'image
            if ($costume->image_url && !str_starts_with($costume->image_url, 'http')) {
                $costume->image_url = url('storage/' . $costume->image_url);
            }

            return response()->json($costume, 200);

        } catch (\Illuminate\Database\Eloquent\ModelNotFoundException $e) {
            return response()->json([
                'error' => 'Costume non trouvé',
                'message' => "Le costume avec l'ID {$id} n'existe pas"
            ], 404);
        } catch (\Exception $e) {
            Log::error('Erreur lors de la récupération du costume: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Mettre à jour un costume
     */
    public function update(Request $request, $id)
    {
        try {
            $costume = Costume::findOrFail($id);

            $validator = Validator::make($request->all(), [
                'name' => 'sometimes|required|string|max:255',
                'description' => 'nullable|string|max:1000',
                'image_url' => 'nullable|string|max:500',
                'image' => 'nullable|image|max:2048',
                'price' => 'sometimes|required|numeric|min:0',
                'date_debut' => 'nullable|date',
                'date_fin' => 'nullable|date|after_or_equal:date_debut',
                'quantite' => 'sometimes|required|integer|min:0',
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'error' => 'Validation échouée',
                    'messages' => $validator->errors()
                ], 422);
            }

            $data = $validator->validated();

            // Gérer le téléchargement de l'image
            if ($request->hasFile('image')) {
                $path = $request->file('image')->store('costumes', 'public');
                $data['image_url'] = $path;
            }

            // Gérer le format de date human-readable provenant d'Android
            if (isset($data['date_debut'])) {
                try {
                    $data['date_debut'] = \Carbon\Carbon::parse($data['date_debut'])->format('Y-m-d');
                } catch (\Exception $e) {
                    //
                }
            }

            if (isset($data['date_fin'])) {
                try {
                    $data['date_fin'] = \Carbon\Carbon::parse($data['date_fin'])->format('Y-m-d');
                } catch (\Exception $e) {
                    //
                }
            }

            unset($data['image']);

            $costume->update($data);

            return response()->json([
                'message' => 'Costume mis à jour avec succès',
                'costume' => $costume
            ], 200);

        } catch (\Illuminate\Database\Eloquent\ModelNotFoundException $e) {
            return response()->json([
                'error' => 'Costume non trouvé',
                'message' => "Le costume avec l'ID {$id} n'existe pas"
            ], 404);
        } catch (\Exception $e) {
            Log::error('Erreur lors de la mise à jour du costume: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Supprimer un costume
     */
    public function destroy($id)
    {
        try {
            $costume = Costume::findOrFail($id);
            $costume->delete();

            return response()->json([
                'message' => 'Costume supprimé avec succès'
            ], 200);

        } catch (\Illuminate\Database\Eloquent\ModelNotFoundException $e) {
            return response()->json([
                'error' => 'Costume non trouvé',
                'message' => "Le costume avec l'ID {$id} n'existe pas"
            ], 404);
        } catch (\Exception $e) {
            Log::error('Erreur lors de la suppression du costume: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Récupérer tous les costumes (sans pagination) - À utiliser avec précaution
     */
    public function all()
    {
        try {
            $costumes = Costume::select('id', 'name', 'price', 'quantite')
                ->limit(100) // Limite de sécurité
                ->get();

            return response()->json($costumes, 200);

        } catch (\Exception $e) {
            Log::error('Erreur lors de la récupération de tous les costumes: ' . $e->getMessage());
            return response()->json([
                'error' => 'Erreur serveur',
                'message' => $e->getMessage()
            ], 500);
        }
    }
}
